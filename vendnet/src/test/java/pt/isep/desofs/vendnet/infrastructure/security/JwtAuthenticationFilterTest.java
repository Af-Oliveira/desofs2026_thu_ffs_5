package pt.isep.desofs.vendnet.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import pt.isep.desofs.vendnet.application.service.JwtService;
import pt.isep.desofs.vendnet.domain.model.user.AccountStatus;
import pt.isep.desofs.vendnet.domain.model.user.Role;
import pt.isep.desofs.vendnet.domain.model.user.User;
import pt.isep.desofs.vendnet.domain.repository.UserRepository;

class JwtAuthenticationFilterTest {

	private JwtAuthenticationFilter filter;
	private JwtService jwtService;
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		jwtService = mock(JwtService.class);
		userRepository = mock(UserRepository.class);
		filter = new JwtAuthenticationFilter(jwtService, userRepository);
		SecurityContextHolder.clearContext();
	}

	@Test
	void doFilter_noAuthHeader_shouldContinue() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		filter.doFilterInternal(request, response, filterChain);

		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void doFilter_invalidToken_shouldContinue() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer invalid-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		when(jwtService.isTokenValid("invalid-token")).thenReturn(false);

		filter.doFilterInternal(request, response, filterChain);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void doFilter_validToken_shouldAuthenticate() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer valid-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		User user = User.builder()
				.id(1L).username("admin").email("admin@vendnet.io").role(Role.ROLE_ADMINISTRATOR)
				.accountStatus(AccountStatus.ACTIVE).build();

		when(jwtService.isTokenValid("valid-token")).thenReturn(true);
		when(jwtService.extractSubject("valid-token")).thenReturn("1");
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		filter.doFilterInternal(request, response, filterChain);

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		assertEquals("admin", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
	}

	@Test
	void doFilter_suspendedUser_shouldNotAuthenticate() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer valid-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		User user = User.builder()
				.id(1L).username("suspended").email("suspended@vendnet.io").role(Role.ROLE_CUSTOMER)
				.accountStatus(AccountStatus.SUSPENDED).build();

		when(jwtService.isTokenValid("valid-token")).thenReturn(true);
		when(jwtService.extractSubject("valid-token")).thenReturn("2");
		when(userRepository.findById(2L)).thenReturn(Optional.of(user));

		filter.doFilterInternal(request, response, filterChain);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void doFilter_lockedUser_shouldNotAuthenticate() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer valid-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		User user = User.builder()
				.id(1L).username("locked").email("locked@vendnet.io").role(Role.ROLE_CUSTOMER)
				.accountStatus(AccountStatus.LOCKED).build();

		when(jwtService.isTokenValid("valid-token")).thenReturn(true);
		when(jwtService.extractSubject("valid-token")).thenReturn("3");
		when(userRepository.findById(3L)).thenReturn(Optional.of(user));

		filter.doFilterInternal(request, response, filterChain);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void doFilter_userNotFound_shouldNotAuthenticate() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer valid-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		when(jwtService.isTokenValid("valid-token")).thenReturn(true);
		when(jwtService.extractSubject("valid-token")).thenReturn("999");
		when(userRepository.findById(999L)).thenReturn(Optional.empty());

		filter.doFilterInternal(request, response, filterChain);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}
}
