package org.openbox.sf5.json;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openbox.sf5.json.exceptions.ApiError;
import org.openbox.sf5.json.exceptions.ItemNotFoundException;
import org.openbox.sf5.json.exceptions.NotAuthenticatedException;
import org.openbox.sf5.json.exceptions.UsersDoNotCoincideException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@EnableWebMvc
@ControllerAdvice(basePackages = { "org.openbox.sf5.json.endpoints", "org.openbox.sf5.json" })
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	private Logger LOG;

	public RestResponseEntityExceptionHandler() {
		LOG = Logger.getLogger(getClass().getSimpleName());
	}

	// In Spring 4.2.6 only with Exception argument method is found and called.
	// Partially taken from
	// http://www.baeldung.com/global-error-handler-in-a-spring-rest-api

	// if item exists when creating
	@ExceptionHandler(value = { IllegalArgumentException.class })
	public ResponseEntity<ApiError> handleIdException(IllegalArgumentException ex, WebRequest request) {
		// String bodyOfResponse = ex.getMessage();
		// // 202
		// return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(),
		// HttpStatus.ACCEPTED, request);
		return constructSerializedException(ex, HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(value = { IllegalStateException.class, IOException.class, SQLException.class })
	public ResponseEntity<ApiError> handleServerException(Exception ex, WebRequest request) {
		return constructSerializedException(ex, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	protected ResponseEntity<ApiError> constructSerializedException(Exception ex, HttpStatus status) {
		ApiError apiError = new ApiError(status, ex);
		LOG.log(Level.INFO, "Exception caught : " + ex.getClass().getSimpleName());
		return new ResponseEntity<>(apiError, status);
	}

	protected ResponseEntity<Object> constructSerializedException(Exception ex, HttpStatus status,
			String customMessage) {

		ApiError apiError = new ApiError(status, ex, customMessage);
		LOG.log(Level.INFO, "Exception caught : " + ex.getClass().getSimpleName());
		return new ResponseEntity<>(apiError, status);
	}

	@ExceptionHandler(NotAuthenticatedException.class)
	public ResponseEntity<ApiError> handleNotAuthenticatedException(NotAuthenticatedException ex, WebRequest request) {

		// List<String> errors = new ArrayList<>();
		//
		// ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,
		// ex.getMessage(), errors);
		// return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
		return constructSerializedException(ex, HttpStatus.UNAUTHORIZED);

	}

	@ExceptionHandler(value = { UsersDoNotCoincideException.class })
	public ResponseEntity<ApiError> handleDifferentUser(UsersDoNotCoincideException ex, WebRequest request) {
		// String bodyOfResponse = ex.getMessage();
		// // 406
		// return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(),
		// HttpStatus.NOT_ACCEPTABLE, request);
		return constructSerializedException(ex, HttpStatus.NOT_ACCEPTABLE);

	}

	@ExceptionHandler(value = { ItemNotFoundException.class })
	public ResponseEntity<ApiError> handleUserNotFound(Exception ex, WebRequest request) {
		// String bodyOfResponse = ex.getMessage();
		//
		// return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(),
		// HttpStatus.NO_CONTENT, request);

		return constructSerializedException(ex, HttpStatus.NOT_FOUND);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = "No handler found for " + ex.getMethod() + " " + request.getContextPath();

		// ApiError apiError = new ApiError(HttpStatus.NOT_FOUND,
		// ex.getLocalizedMessage(), error);
		// return new ResponseEntity<Object>(apiError, new HttpHeaders(),
		// apiError.getStatus());

		return constructSerializedException(ex, HttpStatus.NOT_FOUND, error);
	}

	// Default error handler for other unforeseen exceptions.
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest request) {
		return constructSerializedException(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// @Override
	// protected ResponseEntity<Object>
	// handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
	// HttpHeaders headers, HttpStatus status, WebRequest request) {
	//
	// String error = "Mediatype not supported: ";
	// return constructSerializedException(ex, HttpStatus.BAD_REQUEST, error);
	//
	// }

}
