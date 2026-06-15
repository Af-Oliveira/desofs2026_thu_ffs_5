package pt.isep.desofs.vendnet.infrastructure.os;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
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

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final String AUDIT_LOG_DIRECTORY = "audit";

	private final PathValidator pathValidator;

	@Value("${app.storage.base-path:/var/vendnet}")
	private String vendnetRoot;

	@Value("${app.audit-log.hmac-secret:}")
	private String hmacSecret;

	private String generatedHmacSecret;

	@Override
	public void rotate() {
		try {
			Path logsDir = Paths.get(vendnetRoot, "logs", AUDIT_LOG_DIRECTORY).toRealPath();
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
		} catch (IOException | SecurityException e) {
			log.error("Audit log rotation failed", e);
		}
	}

	@Override
	public void compressAfterDays(int days) {
		try {
			Path logsDir = Paths.get(vendnetRoot, "logs", AUDIT_LOG_DIRECTORY).toRealPath();
			Instant cutoff = Instant.now().minus(days, ChronoUnit.DAYS);

			try (Stream<Path> files = Files.list(logsDir)) {
				files.filter(Files::isRegularFile)
						.filter(f -> f.getFileName().toString().endsWith(".log"))
						.filter(f -> !f.getFileName().toString().contains(".gz"))
						.filter(f -> isOlderThan(f, cutoff))
						.forEach(this::compressAndSign);
			}
		} catch (IOException e) {
			log.error("Log compression failed", e);
		}
	}

	@Override
	public void deleteAfterDays(int days) {
		try {
			Path logsDir = Paths.get(vendnetRoot, "logs", AUDIT_LOG_DIRECTORY).toRealPath();
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
									} catch (IOException e) {
										return false;
									}
								})
						.forEach(
								f -> {
									try {
										Files.deleteIfExists(f);
										log.info("Deleted old log: {}", f.getFileName());
									} catch (IOException e) {
										log.debug("Skipping old log during deletion: {}", f, e);
									}
								});
			}

			log.info("Old log deletion completed (retention: {} days)", days);
		} catch (IOException | SecurityException e) {
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
		} catch (IOException e) {
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
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
			log.error("Failed to compress/sign: {}", file.getFileName(), e);
		}
	}

	private String computeHmac(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac mac = Mac.getInstance("HmacSHA256");
		SecretKeySpec keySpec =
				new SecretKeySpec(
						resolveHmacSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
		mac.init(keySpec);
		byte[] hmacBytes = mac.doFinal(data);
		return HexFormat.of().formatHex(hmacBytes);
	}

	private String resolveHmacSecret() {
		if (hmacSecret != null && !hmacSecret.isBlank()) {
			return hmacSecret;
		}
		if (generatedHmacSecret != null) {
			return generatedHmacSecret;
		}

		byte[] generatedSecret = new byte[32];
		SECURE_RANDOM.nextBytes(generatedSecret);
		generatedHmacSecret = Base64.getEncoder().encodeToString(generatedSecret);
		return generatedHmacSecret;
	}
}
