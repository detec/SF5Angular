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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handling.
 *
 * @author Andrii Duplyk
 *
 */
@EnableWebMvc
@ControllerAdvice(basePackages = { "org.openbox.sf5.json.endpoints", "org.openbox.sf5.json" })
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	private Logger log;

	public RestResponseEntityExceptionHandler() {
		log = Logger.getLogger(getClass().getSimpleName());
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		String error = "No handler found for " + ex.getMessage() + " " + request.getContextPath();

		return constructSerializedException(ex, HttpStatus.BAD_REQUEST, error);
	}

	// In Spring 4.2.6 only with Exception argument method is found and called.
	// Partially taken from
	// http://www.baeldung.com/global-error-handler-in-a-spring-rest-api

	/**
	 * 400 code
	 *
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(value = { IllegalArgumentException.class })
	public ResponseEntity<ApiError> handleIdException(IllegalArgumentException ex, WebRequest request) {
		return constructSerializedException(ex, HttpStatus.BAD_REQUEST);
	}

	/**
	 * 500 code
	 *
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(value = { IllegalStateException.class, IOException.class, SQLException.class })
	public ResponseEntity<ApiError> handleServerException(Exception ex, WebRequest request) {
		return constructSerializedException(ex, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	private ResponseEntity<ApiError> constructSerializedException(Exception ex, HttpStatus status) {
		ApiError apiError = new ApiError(status, ex);
		log.log(Level.INFO, "Exception caught : " + ex.getClass().getSimpleName());
		return new ResponseEntity<>(apiError, status);
	}

	private ResponseEntity<Object> constructSerializedException(Exception ex, HttpStatus status, String customMessage) {

		ApiError apiError = new ApiError(status, ex, customMessage);
		log.log(Level.INFO, "Exception caught : " + ex.getClass().getSimpleName());
		return new ResponseEntity<>(apiError, status);
	}

	/**
	 * 401 code
	 *
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(NotAuthenticatedException.class)
	public ResponseEntity<ApiError> handleNotAuthenticatedException(NotAuthenticatedException ex, WebRequest request) {
		return constructSerializedException(ex, HttpStatus.UNAUTHORIZED);

	}

	/**
	 * 406 code
	 *
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(value = { UsersDoNotCoincideException.class })
	public ResponseEntity<ApiError> handleDifferentUser(UsersDoNotCoincideException ex, WebRequest request) {
		return constructSerializedException(ex, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * 404 code
	 *
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(value = { ItemNotFoundException.class })
	public ResponseEntity<ApiError> handleUserNotFound(Exception ex, WebRequest request) {
		return constructSerializedException(ex, HttpStatus.NOT_FOUND);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = "No handler found for " + ex.getMethod() + " " + request.getContextPath();
		return constructSerializedException(ex, HttpStatus.NOT_FOUND, error);
	}

	/**
	 * Other exceptions, 500 code.
	 *
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest request) {
		return constructSerializedException(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
