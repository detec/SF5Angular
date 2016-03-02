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

	// http://docs.spring.io/spring-security/site/docs/4.0.3.RELEASE/guides/html5/form.html
	/*
	 * @Override public void addViewControllers(ViewControllerRegistry registry)
	 * { registry.addViewController("/login").setViewName("login");
	 * registry.setOrder(Ordered.HIGHEST_PRECEDENCE); }
	 */

	// <bean id="contentNegotiationManager"
	// class="org.springframework.web.accept.ContentNegotiationManagerFactoryBean">
	// <property name="favorPathExtension" value="false" />
	// <property name="favorParameter" value="true" />
	// <property name="parameterName" value="mediaType" />
	// <property name="ignoreAcceptHeader" value="false"/>
	// <property name="useJaf" value="false"/>
	// <property name="defaultContentType" value="application/json" />
	//
	// <property name="mediaTypes">
	// <map>
	// <entry key="html" value="text/html" />
	// <entry key="json" value="application/json" />
	// <entry key="xml" value="application/xml" />
	// </map>
	// </property>
	// </bean>

	// @Override
	// public void configureContentNegotiation(ContentNegotiationConfigurer
	// configurer) {
	// configurer
	//
	// .favorPathExtension(false)
	//
	// .favorParameter(true)
	//
	// .parameterName("mediaType")
	//
	// .ignoreAcceptHeader(false)
	//
	// .useJaf(false)
	//
	// .defaultContentType(MediaType.APPLICATION_JSON)
	//
	// .mediaType("xml", MediaType.APPLICATION_XML)
	//
	// .mediaType("json", MediaType.APPLICATION_JSON)
	//
	// .mediaType("html", MediaType.TEXT_HTML);
	// ;
	// }

}
