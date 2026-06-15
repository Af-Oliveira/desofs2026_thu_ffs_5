package pt.isep.desofs.vendnet.infrastructure.os;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
			throw new ReportDirectoryException(
					"Failed to create report directory: " + e.getMessage(), e);
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
			cleanupReportTree(reportsRoot, cutoff);
		} catch (IOException | SecurityException e) {
			log.error("Report cleanup failed", e);
		}
	}

	private void cleanupReportTree(Path reportsRoot, LocalDate cutoff) throws IOException {
		try (Stream<Path> types = Files.list(reportsRoot)) {
			types.filter(Files::isDirectory)
					.forEach(typeDir -> cleanupTypeDirectory(typeDir, cutoff));
		}
	}

	private void cleanupTypeDirectory(Path typeDir, LocalDate cutoff) {
		try (Stream<Path> years = Files.list(typeDir)) {
			years.filter(Files::isDirectory)
					.forEach(yearDir -> cleanupYearDirectory(yearDir, cutoff));
		} catch (IOException e) {
			log.debug("Skipping report type directory during cleanup: {}", typeDir, e);
		}
	}

	private void cleanupYearDirectory(Path yearDir, LocalDate cutoff) {
		try (Stream<Path> months = Files.list(yearDir)) {
			months.filter(Files::isDirectory)
					.forEach(monthDir -> cleanupMonthDirectory(yearDir, monthDir, cutoff));
		} catch (IOException e) {
			log.debug("Skipping report year directory during cleanup: {}", yearDir, e);
		}
	}

	private void cleanupMonthDirectory(Path yearDir, Path monthDir, LocalDate cutoff) {
		try (Stream<Path> days = Files.list(monthDir)) {
			days.filter(Files::isDirectory)
					.forEach(dayDir -> cleanupDayDirectory(yearDir, monthDir, dayDir, cutoff));
		} catch (IOException e) {
			log.debug("Skipping report month directory during cleanup: {}", monthDir, e);
		}
	}

	private void cleanupDayDirectory(Path yearDir, Path monthDir, Path dayDir, LocalDate cutoff) {
		try {
			LocalDate dirDate = parseReportDate(yearDir, monthDir, dayDir);
			if (dirDate.isBefore(cutoff)) {
				deleteRecursively(dayDir);
				log.info("Cleaned up old report dir: {}", dayDir);
			}
		} catch (DateTimeParseException | IOException e) {
			log.debug("Skipping report day directory during cleanup: {}", dayDir, e);
		}
	}

	private LocalDate parseReportDate(Path yearDir, Path monthDir, Path dayDir) {
		String year = yearDir.getFileName().toString();
		String month = monthDir.getFileName().toString();
		String day = dayDir.getFileName().toString();
		return LocalDate.parse(year + "-" + month + "-" + day);
	}

	private void deleteRecursively(Path root) throws IOException {
		try (Stream<Path> files = Files.walk(root)) {
			files.sorted(Comparator.reverseOrder()).forEach(this::deleteIfExists);
		}
	}

	private void deleteIfExists(Path file) {
		try {
			Files.deleteIfExists(file);
		} catch (IOException e) {
			log.debug("Skipping report path during cleanup: {}", file, e);
		}
	}
}
