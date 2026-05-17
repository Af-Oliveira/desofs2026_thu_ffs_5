package pt.isep.desofs.vendnet.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

class TaintAwareHttpServletResponseWrapperTest {

	private MockHttpServletResponse response;
	private TaintAwareHttpServletResponseWrapper wrapper;

	@BeforeEach
	void setUp() {
		response = new MockHttpServletResponse();
		wrapper = new TaintAwareHttpServletResponseWrapper(response);
	}

	@Test
	void getOutputStream_shouldWriteBytes() throws IOException {
		wrapper.getOutputStream().write(65);
		assertEquals(1, response.getContentAsByteArray().length);
	}

	@Test
	void getWriter_shouldWriteContent() throws IOException {
		wrapper.getWriter().write("hello");
		wrapper.getWriter().flush();
		assertEquals("hello", response.getContentAsString());
	}

	@Test
	void getOutputStream_calledTwice_shouldReturnSameStream() throws IOException {
		var os1 = wrapper.getOutputStream();
		var os2 = wrapper.getOutputStream();
		assertEquals(os1, os2);
	}

	@Test
	void getWriter_afterGetOutputStream_shouldThrow() {
		wrapper.getOutputStream();
		assertThrows(IllegalStateException.class, () -> wrapper.getWriter());
	}

	@Test
	void getOutputStream_afterGetWriter_shouldThrow() throws IOException {
		wrapper.getWriter();
		assertThrows(IllegalStateException.class, () -> wrapper.getOutputStream());
	}
}