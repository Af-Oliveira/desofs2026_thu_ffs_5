package pt.isep.desofs.vendnet.api.controller;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pt.isep.desofs.vendnet.api.view.ApiError;
import pt.isep.desofs.vendnet.domain.exception.AccountLockedException;
import pt.isep.desofs.vendnet.domain.exception.DisabledException;
import pt.isep.desofs.vendnet.domain.exception.MachineOfflineException;
import pt.isep.desofs.vendnet.domain.exception.OutOfStockException;
import pt.isep.desofs.vendnet.domain.exception.PaymentDeclinedException;
import pt.isep.desofs.vendnet.domain.exception.UnauthorizedException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ApiError> handleAuthorizationDenied(AuthorizationDeniedException ex) {
		ApiError error =
				ApiError.builder()
						.status(HttpStatus.FORBIDDEN.value())
						.error("Forbidden")
						.message("Access Denied")
						.timestamp(LocalDateTime.now())
						.build();
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex) {
		ApiError error =
				ApiError.builder()
						.status(HttpStatus.UNAUTHORIZED.value())
						.error("Unauthorized")
						.message(ex.getMessage())
						.timestamp(LocalDateTime.now())
						.build();
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(AccountLockedException.class)
	public ResponseEntity<ApiError> handleAccountLocked(AccountLockedException ex) {
		ApiError error =
				ApiError.builder()
						.status(HttpStatus.UNAUTHORIZED.value())
						.error("Account Locked")
						.message(ex.getMessage())
						.timestamp(LocalDateTime.now())
						.build();
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<ApiError> handleDisabled(DisabledException ex) {
		ApiError error =
				ApiError.builder()
						.status(HttpStatus.UNAUTHORIZED.value())
						.error("Account Disabled")
						.message(ex.getMessage())
						.timestamp(LocalDateTime.now())
						.build();
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
		ApiError error =
				ApiError.builder()
						.status(HttpStatus.BAD_REQUEST.value())
						.error("Bad Request")
						.message(ex.getMessage())
						.timestamp(LocalDateTime.now())
						.build();
		return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> handleMessageNotReadable(
			org.springframework.http.converter.HttpMessageNotReadableException ex) {
		ApiError error =
				ApiError.builder()
						.status(HttpStatus.BAD_REQUEST.value())
						.error("Bad Request")
						.message("Invalid request body")
						.timestamp(LocalDateTime.now())
						.build();
		return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
		String message =
				ex.getBindingResult().getFieldErrors().stream()
						.map(e -> e.getField() + ": " + e.getDefaultMessage())
						.reduce((a, b) -> a + "; " + b)
						.orElse("Validation failed");

		ApiError error =
				ApiError.builder()
						.status(HttpStatus.BAD_REQUEST.value())
						.error("Validation Error")
						.message(message)
						.timestamp(LocalDateTime.now())
						.build();
		return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneric(Exception ex) {
		log.error("Unhandled exception", ex);
		ApiError error =
				ApiError.builder()
						.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
						.error("Internal Server Error")
						.message("An unexpected error occurred")
						.timestamp(LocalDateTime.now())
						.build();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	@ExceptionHandler(OutOfStockException.class)
	public ResponseEntity<ApiError> handleOutOfStock(OutOfStockException ex) {
		ApiError error =
				ApiError.builder()
						.status(HttpStatus.CONFLICT.value())
						.error("Conflict")
						.message(ex.getMessage())
						.timestamp(LocalDateTime.now())
						.build();
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	@ExceptionHandler(MachineOfflineException.class)
	public ResponseEntity<ApiError> handleMachineOffline(MachineOfflineException ex) {
		ApiError error =
				ApiError.builder()
						.status(HttpStatus.CONFLICT.value())
						.error("Conflict")
						.message(ex.getMessage())
						.timestamp(LocalDateTime.now())
						.build();
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	@ExceptionHandler(PaymentDeclinedException.class)
	public ResponseEntity<ApiError> handlePaymentDeclined(PaymentDeclinedException ex) {
		ApiError error =
				ApiError.builder()
						.status(HttpStatus.PAYMENT_REQUIRED.value())
						.error("Payment Required")
						.message(ex.getMessage())
						.timestamp(LocalDateTime.now())
						.build();
		return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(error);
	}
}
