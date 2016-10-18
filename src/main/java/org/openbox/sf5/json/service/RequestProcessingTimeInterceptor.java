package org.openbox.sf5.json.service;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

// http://www.journaldev.com/2676/spring-mvc-interceptors-example-handlerinterceptor-and-handlerinterceptoradapter
/**
 * Class for logging request/response
 *
 * @author Andrii Duplyk
 *
 */
public class RequestProcessingTimeInterceptor extends HandlerInterceptorAdapter {

	private Logger logger = Logger.getLogger(RequestProcessingTimeInterceptor.class.getName());

	private static final String CONSTANT_REQUEST_URL = "Request URL::";
	private static final String CONSTANT_METHOD = ", Method: ";
	private static final String CONSTANT_RESPONSE_STATUS = ", Response status: ";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info(CONSTANT_REQUEST_URL + request.getRequestURL().toString() + CONSTANT_METHOD + request.getMethod()
				+ ":: Start Time=" + System.currentTimeMillis());
		request.setAttribute("startTime", startTime);
		// if returned false, we need to make sure 'response' is sent
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		logger.info(CONSTANT_REQUEST_URL + request.getRequestURL().toString() + CONSTANT_RESPONSE_STATUS
				+ response.getStatus() + CONSTANT_METHOD + request.getMethod() + " Sent to Handler :: Current Time="
				+ System.currentTimeMillis());
		// we can add attributes in the modelAndView and use that in the view
		// page
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		long startTime = (Long) request.getAttribute("startTime");
		logger.info(CONSTANT_REQUEST_URL + request.getRequestURL().toString() + CONSTANT_METHOD + request.getMethod()
				+ CONSTANT_RESPONSE_STATUS + response.getStatus() + ":: End Time=" + System.currentTimeMillis());
		logger.info(CONSTANT_REQUEST_URL + request.getRequestURL().toString() + CONSTANT_METHOD + request.getMethod()
				+ CONSTANT_RESPONSE_STATUS + response.getStatus() + ":: Time Taken="
				+ (System.currentTimeMillis() - startTime));
	}

}
