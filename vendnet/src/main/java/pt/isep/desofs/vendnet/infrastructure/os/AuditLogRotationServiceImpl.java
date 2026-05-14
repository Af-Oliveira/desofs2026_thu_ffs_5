package pt.isep.desofs.vendnet.infrastructure.os;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogRotationServiceImpl implements AuditLogRotationService {

	private final PathValidator pathValidator;

	@Value("${app.storage.base-path:/var/vendnet}")
	private String vendnetRoot;

	@Override
	public void rotate() {
		try {
			Path logsDir = Paths.get(vendnetRoot, "logs", "audit").toRealPath();
			Path sandbox = Paths.get(vendnetRoot).toRealPath();

			if (!pathValidator.isValidPath(logsDir, sandbox)) {
				throw new SecurityException("Log rotation outside sandbox");
			}

			try (Stream<Path> files = Files.list(logsDir)) {
				files.filter(Files::isRegularFile)
						.filter(f -> f.getFileName().toString().endsWith(".log"))
						.filter(f -> !f.getFileName().toString().contains(".gz"))
						.filter(this::isOlderThanOneDay)
						.forEach(this::compressAndSign);
			}

			log.info("Audit log rotation completed");
		} catch (Exception e) {
			log.error("Audit log rotation failed", e);
		}
	}

	@Override
	public void compressAfterDays(int days) {
		try {
			Path logsDir = Paths.get(vendnetRoot, "logs", "audit").toRealPath();
			Instant cutoff = Instant.now().minus(days, ChronoUnit.DAYS);

			try (Stream<Path> files = Files.list(logsDir)) {
				files.filter(Files::isRegularFile)
						.filter(f -> f.getFileName().toString().endsWith(".log"))
						.filter(f -> !f.getFileName().toString().contains(".gz"))
						.filter(f -> isOlderThan(f, cutoff))
						.forEach(this::compressAndSign);
			}
		} catch (Exception e) {
			log.error("Log compression failed", e);
		}
	}

	@Override
	public void deleteAfterDays(int days) {
		try {
			Path logsDir = Paths.get(vendnetRoot, "logs", "audit").toRealPath();
			Path sandbox = Paths.get(vendnetRoot).toRealPath();

			if (!pathValidator.isValidPath(logsDir, sandbox)) {
				throw new SecurityException("Log deletion outside sandbox");
			}

			LocalDateTime cutoff = LocalDateTime.now().minusDays(days);

			try (Stream<Path> files = Files.list(logsDir)) {
				files.filter(Files::isRegularFile)
						.filter(
								f -> {
									try {
										BasicFileAttributes attr =
												Files.readAttributes(f, BasicFileAttributes.class);
										LocalDateTime fileTime =
												LocalDateTime.ofInstant(
														attr.lastModifiedTime().toInstant(),
														ZoneId.systemDefault());
										return fileTime.isBefore(cutoff);
									} catch (Exception e) {
										return false;
									}
								})
						.forEach(
								f -> {
									try {
										Files.deleteIfExists(f);
										log.info("Deleted old log: {}", f.getFileName());
									} catch (Exception ignored) {
										/* ok */
									}
								});
			}

			log.info("Old log deletion completed (retention: {} days)", days);
		} catch (Exception e) {
			log.error("Log deletion failed", e);
		}
	}

	private boolean isOlderThanOneDay(Path file) {
		return isOlderThan(file, Instant.now().minus(1, ChronoUnit.DAYS));
	}

	private boolean isOlderThan(Path file, Instant cutoff) {
		try {
			BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
			return attr.lastModifiedTime().toInstant().isBefore(cutoff);
		} catch (Exception e) {
			return false;
		}
	}

	private void compressAndSign(Path file) {
		try {
			byte[] content = Files.readAllBytes(file);
			String hmac = computeHmac(content);

			Path gzFile = Paths.get(file.toString() + ".gz");
			try (GZIPOutputStream gzos =
					new GZIPOutputStream(new java.io.FileOutputStream(gzFile.toFile()))) {
				gzos.write(content);
			}

			Path hmacFile = Paths.get(file.toString() + ".hmac");
			Files.writeString(hmacFile, hmac);

			Files.deleteIfExists(file);
			log.info("Compressed and signed: {}", file.getFileName());
		} catch (Exception e) {
			log.error("Failed to compress/sign: {}", file.getFileName(), e);
		}
	}

	private String computeHmac(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
		String secret = "hmac-signing-key-placeholder";
		Mac mac = Mac.getInstance("HmacSHA256");
		SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
		mac.init(keySpec);
		byte[] hmacBytes = mac.doFinal(data);
		return HexFormat.of().formatHex(hmacBytes);
	}
}
