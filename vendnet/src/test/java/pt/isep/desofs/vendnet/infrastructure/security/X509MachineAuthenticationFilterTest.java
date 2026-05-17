package pt.isep.desofs.vendnet.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class X509MachineAuthenticationFilterTest {

	private X509MachineAuthenticationFilter filter;

	@BeforeEach
	void setUp() {
		filter = new X509MachineAuthenticationFilter();
		SecurityContextHolder.clearContext();
	}

	@Test
	void doFilter_withValidCert_shouldSetAuthentication() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		X509Certificate cert = mock(X509Certificate.class);
		X500Principal principal = new X500Principal("CN=VM-001,O=VendNet,C=PT");
		when(cert.getSubjectX500Principal()).thenReturn(principal);
		request.setAttribute("jakarta.servlet.request.X509Certificate", new X509Certificate[]{cert});

		filter.doFilterInternal(request, response, filterChain);

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		assertEquals("VM-001", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
	}

	@Test
	void doFilter_withoutCert_shouldContinue() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		filter.doFilterInternal(request, response, filterChain);

		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void doFilter_certWithoutCN_shouldReturn403() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		X509Certificate cert = mock(X509Certificate.class);
		X500Principal principal = new X500Principal("O=VendNet,C=PT");
		when(cert.getSubjectX500Principal()).thenReturn(principal);
		request.setAttribute("jakarta.servlet.request.X509Certificate", new X509Certificate[]{cert});

		filter.doFilterInternal(request, response, filterChain);

		assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
	}
}