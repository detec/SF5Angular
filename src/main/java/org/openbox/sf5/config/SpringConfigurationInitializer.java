package org.openbox.sf5.config;

import javax.servlet.Filter;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class SpringConfigurationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { AppConfiguration.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { MvcConfiguration.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/${jaxrs.path}/" };
		// we use Angular application in the root.
	}

	// next is added from
	// http://stackoverflow.com/questions/23892140/spring-jsf-integration-pure-java-config-no-web-xml
	@Override
	protected Filter[] getServletFilters() {

		return new Filter[] { new CharacterEncodingFilter() };
	}

}
