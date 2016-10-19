package org.openbox.sf5.config;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

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

	// http://websystique.com/springmvc/spring-mvc-4-file-upload-example-using-multipartconfigelement/
	@Override
	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
		registration.setMultipartConfig(getMultipartConfigElement());
	}

	private MultipartConfigElement getMultipartConfigElement() {
		MultipartConfigElement multipartConfigElement = new MultipartConfigElement(LOCATION, MAX_FILE_SIZE,
				MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD);
		return multipartConfigElement;
	}

	private static final String LOCATION = ""; // Temporary location
												// where files will be
												// stored

	private static final long MAX_FILE_SIZE = 5242880; // 5MB : Max file size.
														// Beyond that size
														// spring will throw
														// exception.
	private static final long MAX_REQUEST_SIZE = 20971520; // 20MB : Total
															// request size
															// containing Multi
															// part.

	private static final int FILE_SIZE_THRESHOLD = 5242880; // Size threshold
															// after
	// which files will be
	// written to disk

}
