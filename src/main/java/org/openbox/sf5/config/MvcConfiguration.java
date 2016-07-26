package org.openbox.sf5.config;

import java.util.List;

import org.openbox.sf5.common.JsonObjectFiller;
import org.openbox.sf5.json.service.CustomObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.databind.SerializationFeature;

//@EnableWebMvc
//@Configuration
// @ImportResource("/WEB-INF/sf5-servlet.xml")
//@ComponentScan(basePackages = { "org.openbox.sf5.application" }) // https://www.luckyryan.com/2013/02/07/migrate-spring-mvc-servlet-xml-to-java-config/
// http://stackoverflow.com/questions/22274972/configure-requestmappinghandlermapping-to-not-decode-url
// - rather interesting
public class MvcConfiguration extends WebMvcConfigurerAdapter {

	@Autowired
	private CustomObjectMapper customObjectMapper;

	// we use this method to enable forwarding to the “default” Servlet. The
	// “default” Serlvet is used to handle static content such as CSS, HTML and
	// images.
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	// used in
	// http://codehustler.org/blog/spring-security-tutorial-form-login-java-config/
	@Bean
	public InternalResourceViewResolver getInternalResourceViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setSuffix(".jsp");
		return resolver;
	}

	// http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()

				.dateFormat(JsonObjectFiller.getJsonDateFormatter())

				.indentOutput(true);

		MappingJackson2HttpMessageConverter mc = new MappingJackson2HttpMessageConverter(builder.build());
		mc.setObjectMapper(customObjectMapper);

		MappingJackson2XmlHttpMessageConverter mcxml = new MappingJackson2XmlHttpMessageConverter(
				Jackson2ObjectMapperBuilder.xml().build().configure(SerializationFeature.INDENT_OUTPUT, true));

		converters.add(mc);
		converters.add(mcxml);

	}

	// http://www.programcreek.com/java-api-examples/index.php?source_dir=hydra-java-master/hydra-spring/src/test/java/de/escalon/hypermedia/spring/hydra/HydraMessageConverterTest.java
	// @Override
	// public void
	// configureHandlerExceptionResolvers(List<HandlerExceptionResolver>
	// exceptionResolvers) {
	//
	// final ExceptionHandlerExceptionResolver resolver = new
	// ExceptionHandlerExceptionResolver();
	// resolver.setWarnLogCategory(resolver.getClass().getName());
	// exceptionResolvers.add(resolver);
	// }

}
