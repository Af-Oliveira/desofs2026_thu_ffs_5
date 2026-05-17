package pt.isep.desofs.vendnet.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class CorrelationIdFilterTest {

	private CorrelationIdFilter filter;

	@BeforeEach
	void setUp() {
		filter = new CorrelationIdFilter();
		MDC.clear();
	}

	@Test
	void doFilter_withCorrelationId_shouldUseProvidedId() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("X-Correlation-Id", "test-correlation-123");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		filter.doFilterInternal(request, response, filterChain);

		assertEquals("test-correlation-123", response.getHeader("X-Correlation-Id"));
	}

	@Test
	void doFilter_withoutCorrelationId_shouldGenerateOne() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		filter.doFilterInternal(request, response, filterChain);

		assertNotNull(response.getHeader("X-Correlation-Id"));
	}

	@Test
	void doFilter_blankCorrelationId_shouldGenerateOne() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("X-Correlation-Id", "   ");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = mock(FilterChain.class);

		filter.doFilterInternal(request, response, filterChain);

		assertNotNull(response.getHeader("X-Correlation-Id"));
	}
}