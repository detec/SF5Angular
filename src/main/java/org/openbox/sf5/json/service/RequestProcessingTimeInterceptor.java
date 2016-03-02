package org.openbox.sf5.json.service;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

// http://www.journaldev.com/2676/spring-mvc-interceptors-example-handlerinterceptor-and-handlerinterceptoradapter
public class RequestProcessingTimeInterceptor extends HandlerInterceptorAdapter {

	// private static final Logger logger =
	// LoggerFactory.getLogger(RequestProcessingTimeInterceptor.class);
	private Logger logger = Logger.getLogger(RequestProcessingTimeInterceptor.class.getName());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info("Request URL::" + request.getRequestURL().toString() + ", Method: " + request.getMethod()
				+ ":: Start Time=" + System.currentTimeMillis());
		request.setAttribute("startTime", startTime);
		// if returned false, we need to make sure 'response' is sent
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		System.out.println("Request URL::" + request.getRequestURL().toString() + ", Method: "
				+ " Sent to Handler :: Current Time=" + System.currentTimeMillis());
		// we can add attributes in the modelAndView and use that in the view
		// page
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		long startTime = (Long) request.getAttribute("startTime");
		logger.info("Request URL::" + request.getRequestURL().toString() + ", Method: " + ":: End Time="
				+ System.currentTimeMillis());
		logger.info("Request URL::" + request.getRequestURL().toString() + ", Method: " + ":: Time Taken="
				+ (System.currentTimeMillis() - startTime));
	}

}
