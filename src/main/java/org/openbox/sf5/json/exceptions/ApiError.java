package org.openbox.sf5.json.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

public class ApiError {
	private HttpStatus status;
	private String message;
	private List<String> errors = new ArrayList<>();;

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public ApiError(HttpStatus status, Exception ex) {
		super();
		this.status = status;
		this.message = ex.getMessage();
		fillStackTrace(ex);
	}

	// overloaded constructor to send customized error messages.
	public ApiError(HttpStatus status, Exception ex, String error) {
		super();
		this.status = status;
		this.message = error;
		fillStackTrace(ex);
	}

	private void fillStackTrace(Exception ex) {
		StackTraceElement elements[] = ex.getStackTrace();
		for (int i = 0, n = elements.length; i < n; i++) {

			String stackTraceLine = elements[i].getFileName() + ":" + elements[i].getLineNumber() + ">> "
					+ elements[i].getMethodName() + "() ";

			this.errors.add(stackTraceLine);
		}
	}

}
