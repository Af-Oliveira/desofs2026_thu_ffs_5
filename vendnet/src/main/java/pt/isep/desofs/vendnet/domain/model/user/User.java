package pt.isep.desofs.vendnet.domain.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	private static final int MAX_FAILED_ATTEMPTS = 5;
	private static final int LOCK_DURATION_MINUTES = 30;
	private static final int LOCK_WINDOW_MINUTES = 15;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50)
	private String username;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, length = 100)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private AccountStatus accountStatus = AccountStatus.ACTIVE;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Column(nullable = false)
	@Builder.Default
	private int failedAttempts = 0;

	@Column private LocalDateTime lockTime;

	@Column private LocalDateTime lastFailedAttemptTime;

	@Column(length = 32)
	private String totpSecret;

	public void checkAccountStatus() {
		if (this.accountStatus == AccountStatus.SUSPENDED) {
			throw new pt.isep.desofs.vendnet.domain.exception.DisabledException(
					"Account is suspended");
		}
		if (this.accountStatus == AccountStatus.LOCKED) {
			if (this.lockTime != null
					&& this.lockTime.plusMinutes(LOCK_DURATION_MINUTES).isBefore(LocalDateTime.now())) {
				resetLockout();
				return;
			}
			throw new pt.isep.desofs.vendnet.domain.exception.AccountLockedException(
					"Account is temporarily locked. Try again in " + LOCK_DURATION_MINUTES + " minutes.");
		}
	}

	public boolean verifyPassword(String rawPassword, String encodedPassword) {
		return BCrypt.checkpw(rawPassword, this.password);
	}

	public void resetFailedAttempts() {
		this.failedAttempts = 0;
	}

	public void incrementFailedAttempts() {
		LocalDateTime now = LocalDateTime.now();
		if (this.lastFailedAttemptTime != null
				&& this.lastFailedAttemptTime.plusMinutes(LOCK_WINDOW_MINUTES).isBefore(now)) {
			this.failedAttempts = 0;
		}
		this.failedAttempts++;
		this.lastFailedAttemptTime = now;
		if (this.failedAttempts >= MAX_FAILED_ATTEMPTS) {
			lockAccount();
		}
	}

	private void lockAccount() {
		this.accountStatus = AccountStatus.LOCKED;
		this.lockTime = LocalDateTime.now();
	}

	private void resetLockout() {
		this.failedAttempts = 0;
		this.accountStatus = AccountStatus.ACTIVE;
		this.lockTime = null;
		this.lastFailedAttemptTime = null;
	}
}
