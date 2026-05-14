package pt.isep.desofs.vendnet.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.isep.desofs.vendnet.api.dto.AuthResponse;
import pt.isep.desofs.vendnet.api.dto.LoginRequest;
import pt.isep.desofs.vendnet.api.dto.RegisterRequest;
import pt.isep.desofs.vendnet.api.dto.UserResponse;
import pt.isep.desofs.vendnet.domain.exception.AccountLockedException;
import pt.isep.desofs.vendnet.domain.exception.UnauthorizedException;
import pt.isep.desofs.vendnet.domain.model.user.Role;
import pt.isep.desofs.vendnet.domain.model.user.User;
import pt.isep.desofs.vendnet.domain.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;
    private static final int LOCK_WINDOW_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PreAuthorize("permitAll()")
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(Role.ROLE_CUSTOMER)
                .createdAt(now)
                .updatedAt(now)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }

    @PreAuthorize("permitAll()")
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (isAccountLocked(user)) {
            throw new AccountLockedException("Account is temporarily locked. Try again in " + LOCK_DURATION_MINUTES + " minutes.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(user);
            throw new UnauthorizedException("Invalid email or password");
        }

        resetLockout(user);

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private boolean isAccountLocked(User user) {
        if (user.getLockTime() == null) return false;
        if (user.getLockTime().plusMinutes(LOCK_DURATION_MINUTES).isBefore(LocalDateTime.now())) {
            resetLockout(user);
            return false;
        }
        return true;
    }

    private void handleFailedLogin(User user) {
        LocalDateTime now = LocalDateTime.now();
        // Reset counter if last failure is outside the 15-min window
        if (user.getLastFailedAttemptTime() != null &&
                user.getLastFailedAttemptTime().plusMinutes(LOCK_WINDOW_MINUTES).isBefore(now)) {
            user.setFailedAttempts(0);
        }
        user.setFailedAttempts(user.getFailedAttempts() + 1);
        user.setLastFailedAttemptTime(now);
        if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
            user.setLockTime(now);
        }
        userRepository.save(user);
    }

    private void resetLockout(User user) {
        user.setFailedAttempts(0);
        user.setLockTime(null);
        user.setLastFailedAttemptTime(null);
        userRepository.save(user);
    }
}
