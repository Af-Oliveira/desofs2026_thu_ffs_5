package pt.isep.desofs.vendnet.application.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private final SecretKey key;
	private final long expirationMs;
	private final Map<String, Long> blocklist = new ConcurrentHashMap<>();

	public JwtService(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration-ms}") long expirationMs) {
		if (secret.length() < 32) {
			throw new IllegalStateException("JWT secret must be at least 32 characters (256 bits)");
		}
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationMs = expirationMs;
	}

	public String generateToken(String email) {
		return buildToken(email, null);
	}

	public String generateToken(String email, String role) {
		return buildToken(email, role);
	}

	public String generateToken(Long userId, String role) {
		return buildToken(String.valueOf(userId), role);
	}

	public String generateRefreshToken(Long userId) {
		return buildToken("refresh:" + userId, null);
	}

	public long getExpirationSeconds() {
		return expirationMs / 1000;
	}

	private String buildToken(String email, String role) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expirationMs);

		var builder =
				Jwts.builder()
						.subject(email)
						.id(UUID.randomUUID().toString())
						.issuedAt(now)
						.expiration(expiry);

		if (role != null) {
			builder.claim("role", role);
		}

		return builder.signWith(key).compact();
	}

	public String extractEmail(String token) {
		return extractClaims(token).getSubject();
	}

	public String extractSubject(String token) {
		return extractClaims(token).getSubject();
	}

	public String extractJti(String token) {
		return extractClaims(token).getId();
	}

	public String extractRole(String token) {
		return extractClaims(token).get("role", String.class);
	}

	public boolean isTokenValid(String token) {
		try {
			Claims claims = extractClaims(token);
			if (isBlocklisted(claims.getId())) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void blocklistToken(String token) {
		try {
			Claims claims = extractClaims(token);
			long remainingMs = claims.getExpiration().getTime() - System.currentTimeMillis();
			if (remainingMs > 0) {
				blocklist.put(claims.getId(), System.currentTimeMillis() + remainingMs);
			}
		} catch (Exception ignored) {
			// token is already invalid — nothing to blocklist
		}
	}

	public void revokeUserTokens(String email) {
		// Full revocation would require persisting blocklist to Redis.
		// In-memory blocklist is cleared on restart — acceptable for dev phase.
		// Production: iterate Redis keys matching "jwt:revoked:<email>:*" with TTL.
		blocklist.entrySet().removeIf(entry -> entry.getValue() < System.currentTimeMillis());
	}

	private boolean isBlocklisted(String jti) {
		if (jti == null) {
			return false;
		}
		Long expiry = blocklist.get(jti);
		if (expiry == null) {
			return false;
		}
		if (expiry < System.currentTimeMillis()) {
			blocklist.remove(jti);
			return false;
		}
		return true;
	}

	private Claims extractClaims(String token) {
		rejectAlgNone(token);
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
	}

	private void rejectAlgNone(String token) {
		String[] parts = token.split("\\.");
		if (parts.length < 2) {
			return;
		}
		try {
			byte[] headerBytes = Base64.getUrlDecoder().decode(parts[0]);
			String header = new String(headerBytes, StandardCharsets.UTF_8).toLowerCase();
			if (header.contains("\"alg\":\"none\"") || header.contains("\"alg\": \"none\"")) {
				throw new SecurityException("alg:none tokens are not permitted");
			}
		} catch (SecurityException e) {
			throw e;
		} catch (Exception ignored) {
			// header decode failure — treat as non-JWT, let parser reject it
		}
	}
}
