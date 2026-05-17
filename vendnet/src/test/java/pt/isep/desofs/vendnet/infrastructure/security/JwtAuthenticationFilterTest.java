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
				.id(1L).email("admin@vendnet.io").role(Role.ROLE_ADMINISTRATOR)
				.accountStatus(AccountStatus.ACTIVE).build();

		when(jwtService.isTokenValid("valid-token")).thenReturn(true);
		when(jwtService.extractEmail("valid-token")).thenReturn("admin@vendnet.io");
		when(userRepository.findByEmail("admin@vendnet.io")).thenReturn(Optional.of(user));

		filter.doFilterInternal(request, response, filterChain);

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		assertEquals("admin@vendnet.io", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
	}

	@Test
	void doFilter_suspendedUser_shouldNotAuthenticate() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer valid-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		User user = User.builder()
				.id(1L).email("suspended@vendnet.io").role(Role.ROLE_CUSTOMER)
				.accountStatus(AccountStatus.SUSPENDED).build();

		when(jwtService.isTokenValid("valid-token")).thenReturn(true);
		when(jwtService.extractEmail("valid-token")).thenReturn("suspended@vendnet.io");
		when(userRepository.findByEmail("suspended@vendnet.io")).thenReturn(Optional.of(user));

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
				.id(1L).email("locked@vendnet.io").role(Role.ROLE_CUSTOMER)
				.accountStatus(AccountStatus.LOCKED).build();

		when(jwtService.isTokenValid("valid-token")).thenReturn(true);
		when(jwtService.extractEmail("valid-token")).thenReturn("locked@vendnet.io");
		when(userRepository.findByEmail("locked@vendnet.io")).thenReturn(Optional.of(user));

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
		when(jwtService.extractEmail("valid-token")).thenReturn("unknown@vendnet.io");
		when(userRepository.findByEmail("unknown@vendnet.io")).thenReturn(Optional.empty());

		filter.doFilterInternal(request, response, filterChain);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}
}