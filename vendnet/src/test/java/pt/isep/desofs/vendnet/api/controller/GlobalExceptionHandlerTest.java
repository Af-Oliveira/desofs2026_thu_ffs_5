package pt.isep.desofs.vendnet.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import pt.isep.desofs.vendnet.api.view.ApiError;
import pt.isep.desofs.vendnet.domain.exception.AccountLockedException;
import pt.isep.desofs.vendnet.domain.exception.DisabledException;
import pt.isep.desofs.vendnet.domain.exception.MachineOfflineException;
import pt.isep.desofs.vendnet.domain.exception.OutOfStockException;
import pt.isep.desofs.vendnet.domain.exception.PaymentDeclinedException;
import pt.isep.desofs.vendnet.domain.exception.UnauthorizedException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

	private GlobalExceptionHandler handler;

	@BeforeEach
	void setUp() {
		handler = new GlobalExceptionHandler();
	}

	@Test
	void handleUnauthorized_shouldReturn401() {
		ResponseEntity<ApiError> response = handler.handleUnauthorized(new UnauthorizedException("bad creds"));
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertEquals(401, response.getBody().getStatus());
		assertEquals("Unauthorized", response.getBody().getError());
	}

	@Test
	void handleAccountLocked_shouldReturn401() {
		ResponseEntity<ApiError> response = handler.handleAccountLocked(new AccountLockedException("locked"));
		assertEquals(HttpStatus.LOCKED, response.getStatusCode());
		assertEquals(423, response.getBody().getStatus());
		assertEquals("Account Locked", response.getBody().getError());
	}

	@Test
	void handleDisabled_shouldReturn401() {
		ResponseEntity<ApiError> response = handler.handleDisabled(new DisabledException("suspended"));
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		assertEquals(403, response.getBody().getStatus());
		assertEquals("Account Disabled", response.getBody().getError());
	}

	@Test
	void handleAuthorizationDenied_shouldReturn403() {
		ResponseEntity<ApiError> response = handler.handleAuthorizationDenied(new AuthorizationDeniedException("denied"));
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		assertEquals("Forbidden", response.getBody().getError());
	}

	@Test
	void handleIllegalArgument_shouldReturn400() {
		ResponseEntity<ApiError> response = handler.handleIllegalArgument(new IllegalArgumentException("invalid"));
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Bad Request", response.getBody().getError());
	}

	@Test
	void handleOutOfStock_shouldReturn409() {
		ResponseEntity<ApiError> response = handler.handleOutOfStock(new OutOfStockException("no stock"));
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertEquals("Conflict", response.getBody().getError());
	}

	@Test
	void handleMachineOffline_shouldReturn409() {
		ResponseEntity<ApiError> response = handler.handleMachineOffline(new MachineOfflineException("offline"));
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
	}

	@Test
	void handlePaymentDeclined_shouldReturn402() {
		ResponseEntity<ApiError> response = handler.handlePaymentDeclined(new PaymentDeclinedException("declined"));
		assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
		assertEquals("Payment Required", response.getBody().getError());
	}

	@Test
	void handleGeneric_shouldReturn500() {
		ResponseEntity<ApiError> response = handler.handleGeneric(new RuntimeException("oops"));
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Internal Server Error", response.getBody().getError());
		assertEquals("An unexpected error occurred", response.getBody().getMessage());
	}

	@Test
	void handleMessageNotReadable_shouldReturn400() {
		org.springframework.http.converter.HttpMessageNotReadableException ex =
			new org.springframework.http.converter.HttpMessageNotReadableException("bad json", (org.springframework.http.HttpInputMessage) null);
		ResponseEntity<ApiError> response = handler.handleMessageNotReadable(ex);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Invalid request body", response.getBody().getMessage());
	}

	@Test
	void handleValidation_shouldReturn400() throws NoSuchMethodException {
		MethodParameter parameter =
			new MethodParameter(GlobalExceptionHandlerTest.class.getDeclaredMethod("validationTarget", Object.class), 0);
		MethodArgumentNotValidException ex =
			new MethodArgumentNotValidException(parameter, new BeanPropertyBindingResult(new Object(), "obj"));
		ResponseEntity<ApiError> response = handler.handleValidation(ex);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Validation Error", response.getBody().getError());
	}

	@Test
	void allApiErrors_shouldHaveTimestamp() {
		ResponseEntity<ApiError> response = handler.handleUnauthorized(new UnauthorizedException("test"));
		assertNotNull(response.getBody().getTimestamp());
	}

	@SuppressWarnings("unused")
	private void validationTarget(Object obj) {}
}
