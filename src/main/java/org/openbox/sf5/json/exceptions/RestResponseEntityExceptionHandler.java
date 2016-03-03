package org.openbox.sf5.json.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	// if item exists when creating
	@ExceptionHandler(value = { IllegalArgumentException.class })
	protected ResponseEntity<Object> handleIdException(RuntimeException ex, WebRequest request) {
		String bodyOfResponse = ex.getMessage();
		// 202
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.ACCEPTED, request);
	}

	@ExceptionHandler(value = { IllegalStateException.class })
	protected ResponseEntity<Object> handleServerException(RuntimeException ex, WebRequest request) {
		String bodyOfResponse = ex.getMessage();
		// 409
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler(value = { NotAuthenticatedException.class })
	protected ResponseEntity<Object> handleNotAuthenticatedException(RuntimeException ex, WebRequest request) {
		String bodyOfResponse = ex.getMessage();
		// 403
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);

	}

	@ExceptionHandler(value = { UsersDoNotCoincideException.class })
	protected ResponseEntity<Object> handleDifferentUser(RuntimeException ex, WebRequest request) {
		String bodyOfResponse = ex.getMessage();
		// 406
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
	}

	@ExceptionHandler(value = { UserNotFoundException.class, ItemNotFoundException.class })
	protected ResponseEntity<Object> handleUserNotFound(RuntimeException ex, WebRequest request) {
		String bodyOfResponse = ex.getMessage();

		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NO_CONTENT, request);

	}
}
