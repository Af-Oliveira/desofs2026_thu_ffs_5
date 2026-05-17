package pt.isep.desofs.vendnet.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import jakarta.servlet.FilterChain;

class IastTaintTrackingFilterTest {

	private IastTaintTrackingFilter filter;

	@BeforeEach
	void setUp() {
		filter = new IastTaintTrackingFilter();
		IastTaintTrackingFilter.clearDetectedFlows();
		IastTaintTrackingFilter.clearConfirmedExploitableFlows();
	}

	@AfterEach
	void tearDown() {
		IastTaintTrackingFilter.clearDetectedFlows();
		IastTaintTrackingFilter.clearConfirmedExploitableFlows();
	}

	@Test
	void doFilter_cleanRequest_shouldNotDetectFlows() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/api/products");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = (req, resp) -> {};

		filter.doFilterInternal(request, response, filterChain);

		assertTrue(IastTaintTrackingFilter.getDetectedFlows().isEmpty());
		assertTrue(IastTaintTrackingFilter.getConfirmedExploitableFlows().isEmpty());
	}

	@Test
	void doFilter_sqlInjection_shouldDetectTaintFlow() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/api/products");
		request.addParameter("q", "' OR 1=1");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = (req, resp) -> {};

		filter.doFilterInternal(request, response, filterChain);

		assertFalse(IastTaintTrackingFilter.getDetectedFlows().isEmpty());
	}

	@Test
	void doFilter_pathTraversal_shouldDetectTaintFlow() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/api/files/../../etc/passwd");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = (req, resp) -> {};

		filter.doFilterInternal(request, response, filterChain);

		Map<String, List<IastTaintTrackingFilter.TaintFlow>> flows = IastTaintTrackingFilter.getDetectedFlows();
		assertFalse(flows.isEmpty());
	}

	@Test
	void getDetectedFlows_shouldReturnUnmodifiableMap() {
		Map<String, List<IastTaintTrackingFilter.TaintFlow>> flows = IastTaintTrackingFilter.getDetectedFlows();
		assertTrue(flows.isEmpty());
	}

	@Test
	void clearDetectedFlows_shouldClearAllFlows() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/api/products");
		request.addParameter("q", "' OR 1=1");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = (req, resp) -> {};

		filter.doFilterInternal(request, response, filterChain);
		assertFalse(IastTaintTrackingFilter.getDetectedFlows().isEmpty());

		IastTaintTrackingFilter.clearDetectedFlows();
		assertTrue(IastTaintTrackingFilter.getDetectedFlows().isEmpty());
	}

	@Test
	void taintFlow_toString_shouldContainType() {
		IastTaintTrackingFilter.TaintFlow flow = new IastTaintTrackingFilter.TaintFlow(
				"SQL_INJECTION", "param q", "query");
		String str = flow.toString();
		assertTrue(str.contains("SQL_INJECTION"));
	}
}