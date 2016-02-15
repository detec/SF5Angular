package org.openbox.sf5.config;

import javax.servlet.ServletContext;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.multipart.support.MultipartFilter;

// Duplicate SpringSecurityFilter initialization.
public class SpringSecurityInitializer extends AbstractSecurityWebApplicationInitializer
{
	// https://docs.spring.io/spring-security/site/docs/current/reference/html/csrf.html
	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
		insertFilters(servletContext, new MultipartFilter());
	}


}
