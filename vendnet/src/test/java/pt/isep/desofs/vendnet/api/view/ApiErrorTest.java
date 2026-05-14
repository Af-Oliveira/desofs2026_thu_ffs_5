package pt.isep.desofs.vendnet.api.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ApiErrorTest {

	@Test
	void builder_shouldCreateApiErrorWithAllFields() {
		LocalDateTime now = LocalDateTime.now();

		ApiError error = ApiError.builder()
				.status(401)
				.error("Unauthorized")
				.message("Invalid email or password")
				.timestamp(now)
				.build();

		assertEquals(401, error.getStatus());
		assertEquals("Unauthorized", error.getError());
		assertEquals("Invalid email or password", error.getMessage());
		assertEquals(now, error.getTimestamp());
	}

	@Test
	void setters_shouldModifyFields() {
		ApiError error = new ApiError();
		LocalDateTime now = LocalDateTime.now();

		error.setStatus(403);
		error.setError("Forbidden");
		error.setMessage("Access denied");
		error.setTimestamp(now);

		assertEquals(403, error.getStatus());
		assertEquals("Forbidden", error.getError());
		assertEquals("Access denied", error.getMessage());
		assertEquals(now, error.getTimestamp());
	}

	@Test
	void noArgsConstructor_shouldCreateEmpty() {
		ApiError error = new ApiError();

		assertEquals(0, error.getStatus());
		assertNull(error.getError());
		assertNull(error.getMessage());
		assertNull(error.getTimestamp());
	}

	@Test
	void allArgsConstructor_shouldCreateApiError() {
		LocalDateTime now = LocalDateTime.now();
		ApiError error = new ApiError(404, "Not Found", "Resource not found", now);

		assertEquals(404, error.getStatus());
		assertEquals("Not Found", error.getError());
		assertEquals("Resource not found", error.getMessage());
		assertEquals(now, error.getTimestamp());
	}
}
