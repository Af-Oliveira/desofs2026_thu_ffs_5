package pt.isep.desofs.vendnet.infrastructure.os;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportDirectoryServiceImpl implements ReportDirectoryService {

	private static final Set<String> ALLOWED_REPORT_TYPES =
			Set.of("sales", "inventory", "machine", "audit");

	private final PathValidator pathValidator;

	@Value("${app.storage.base-path:/var/vendnet}")
	private String vendnetRoot;

	@Override
	public String createReportDirectory(String reportType) {
		if (!ALLOWED_REPORT_TYPES.contains(reportType)) {
			throw new IllegalArgumentException("Invalid report type: " + reportType);
		}

		try {
			Path root = Paths.get(vendnetRoot);
			Files.createDirectories(root);
			Path sandbox = root.toRealPath();
			LocalDate now = LocalDate.now();
			Path reportPath =
					sandbox.resolve(
									Paths.get(
											"reports",
											reportType,
											String.valueOf(now.getYear()),
											String.format("%02d", now.getMonthValue()),
											String.format("%02d", now.getDayOfMonth())))
							.normalize();
			if (!pathValidator.isValidPath(reportPath, sandbox)) {
				throw new SecurityException("Report path outside sandbox: " + reportPath);
			}

			Files.createDirectories(reportPath);
			log.info("Report directory created: {}", reportPath);
			return reportPath.toString();
		} catch (IOException e) {
			throw new RuntimeException("Failed to create report directory: " + e.getMessage(), e);
		}
	}

	@Override
	public void cleanupOldReports(int retentionDays) {
		try {
			Path reportsRoot = Paths.get(vendnetRoot, "reports").toRealPath();
			Path sandbox = Paths.get(vendnetRoot).toRealPath();

			if (!pathValidator.isValidPath(reportsRoot, sandbox)) {
				throw new SecurityException("Report cleanup outside sandbox");
			}

			LocalDate cutoff = LocalDate.now().minusDays(retentionDays);

			try (Stream<Path> types = Files.list(reportsRoot)) {
				types.filter(Files::isDirectory)
						.forEach(
								typeDir -> {
									try (Stream<Path> years = Files.list(typeDir)) {
										years.filter(Files::isDirectory)
												.forEach(
														yearDir -> {
															try (Stream<Path> months =
																	Files.list(yearDir)) {
																months.filter(Files::isDirectory)
																		.forEach(
																				monthDir -> {
																					try (Stream<
																									Path>
																							days =
																									Files
																											.list(
																													monthDir)) {
																						days.filter(
																										Files
																												::isDirectory)
																								.forEach(
																										dayDir -> {
																											try {
																												String
																														year =
																																yearDir.getFileName()
																																		.toString();
																												String
																														month =
																																monthDir.getFileName()
																																		.toString();
																												String
																														day =
																																dayDir.getFileName()
																																		.toString();
																												LocalDate
																														dirDate =
																																LocalDate
																																		.parse(
																																				year
																																						+ "-"
																																						+ month
																																						+ "-"
																																						+ day);

																												if (dirDate
																														.isBefore(
																																cutoff)) {
																													Files
																															.walk(
																																	dayDir)
																															.sorted(
																																	Comparator
																																			.reverseOrder())
																															.forEach(
																																	f -> {
																																		try {
																																			Files
																																					.deleteIfExists(
																																							f);
																																		} catch (
																																				Exception
																																						ignored) {
																																			/* ok */
																																		}
																																	});
																													log
																															.info(
																																	"Cleaned up old report dir: {}",
																																	dayDir);
																												}
																											} catch (
																													Exception
																															ignored) {
																												/* ok */
																											}
																										});
																					} catch (
																							Exception
																									ignored) {
																						/* ok */
																					}
																				});
															} catch (Exception ignored) {
																/* ok */
															}
														});
									} catch (Exception ignored) {
										/* ok */
									}
								});
			}
		} catch (Exception e) {
			log.error("Report cleanup failed", e);
		}
	}
}
