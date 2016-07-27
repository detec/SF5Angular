package org.openbox.sf5.config;

import javax.servlet.Filter;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

// Now will mark this as deprecated.
public class SpringConfigurationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer

{

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { AppConfiguration.class };
	}

	// let's not use config classes.
	@Override
	protected Class<?>[] getServletConfigClasses() {
		// return new Class[] {
		// // let's use "erroneous" class MvcConfiguration, as in the
		// // original Spring project
		//
		// // ManualWebMvcConfiguration.class - we get trash when we use
		// // multiple config classes.
		// ManualWebMvcConfiguration.class
		//
		// };

		return new Class[] {};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };

	}

	// next is added from
	// http://stackoverflow.com/questions/23892140/spring-jsf-integration-pure-java-config-no-web-xml
	@Override
	protected Filter[] getServletFilters() {

		return new Filter[] { new CharacterEncodingFilter() };
	}

}
