package pt.isep.desofs.vendnet.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pt.isep.desofs.vendnet.application.service.JwtService;
import pt.isep.desofs.vendnet.domain.model.user.AccountStatus;
import pt.isep.desofs.vendnet.domain.model.user.User;
import pt.isep.desofs.vendnet.domain.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7);

		if (!jwtService.isTokenValid(token)) {
			filterChain.doFilter(request, response);
			return;
		}

		String subject = jwtService.extractSubject(token);

		User user = resolveUser(subject);
		if (user == null) {
			log.warn("JWT valid but user not found in DB: {}", subject);
			filterChain.doFilter(request, response);
			return;
		}

		if (user.getAccountStatus() == AccountStatus.SUSPENDED) {
			log.warn("Blocked request from suspended account: {}", user.getUsername());
			jwtService.blocklistToken(token);
			filterChain.doFilter(request, response);
			return;
		}

		if (user.getAccountStatus() == AccountStatus.LOCKED) {
			log.debug("Blocked request from locked account: {}", user.getUsername());
			filterChain.doFilter(request, response);
			return;
		}

		String role = user.getRole().name();

		UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(
						user.getUsername(), null, List.of(new SimpleGrantedAuthority(role)));
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		log.debug("Authenticated user {} with role {}", user.getUsername(), role);

		filterChain.doFilter(request, response);
	}

	private User resolveUser(String subject) {
		try {
			return userRepository.findById(Long.valueOf(subject)).orElse(null);
		} catch (NumberFormatException ignored) {
			return userRepository
					.findByUsername(subject)
					.or(() -> userRepository.findByEmail(subject))
					.orElse(null);
		}
	}
}
