package org.openbox.sf5.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.openbox.sf5.common.JsonObjectFiller;
import org.openbox.sf5.json.service.CustomObjectMapper;
import org.openbox.sf5.json.service.CustomXMLMapper;
import org.openbox.sf5.json.service.RequestProcessingTimeInterceptor;
import org.openbox.sf5.model.Sat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Configuration
@ComponentScan(basePackages = { "org.openbox.sf5.common", "org.openbox.sf5.json" })
public class ManualWebMvcConfiguration extends WebMvcConfigurationSupport {

	@Autowired
	private Jaxb2Marshaller springMarshaller;

	@Bean
	@Primary
	public ObjectMapper customObjectMapper() {
		CustomObjectMapper customObjectMapper = new CustomObjectMapper();
		return customObjectMapper;
	}

	@Bean
	@Primary
	XmlMapper customXMLMapper() {
		CustomXMLMapper xmlMapper = new CustomXMLMapper();
		return xmlMapper;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new RequestProcessingTimeInterceptor());
	}

	// http://stackoverflow.com/questions/22267191/is-it-possible-to-extend-webmvcconfigurationsupport-and-use-webmvcautoconfigurat
	@Override
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		RequestMappingHandlerMapping handlerMapping = super.requestMappingHandlerMapping();
		handlerMapping.setRemoveSemicolonContent(false);
		handlerMapping.setOrder(1);
		return handlerMapping;
	}

	// we use this method to enable forwarding to the “default” Servlet. The
	// “default” Serlvet is used to handle static content such as CSS, HTML and
	// images.
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean
	public InternalResourceViewResolver getInternalResourceViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setSuffix(".jsp");
		return resolver;
	}

	// http://docs.spring.io/spring-security/site/docs/4.0.3.RELEASE/guides/html5/form.html
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}

	/*
	 * Configure ResourceHandlers to serve static resources like CSS/ Javascript
	 * etc...
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/css/**").addResourceLocations("/css/");
		registry.addResourceHandler("/js/**").addResourceLocations("/js/");
	}

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
	//
	// }

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		// This doesn't add any converters
		// super.configureMessageConverters(converters);

		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()

				.dateFormat(JsonObjectFiller.getJsonDateFormatter())

				.indentOutput(true);

		MappingJackson2HttpMessageConverter mc = new MappingJackson2HttpMessageConverter(builder.build());
		// mc.setObjectMapper(customObjectMapper());

		MappingJackson2XmlHttpMessageConverter mcxml = new MappingJackson2XmlHttpMessageConverter(
				Jackson2ObjectMapperBuilder.xml().build());
		mcxml.setObjectMapper(customXMLMapper());

		MarshallingHttpMessageConverter jaxbc = new MarshallingHttpMessageConverter();
		jaxbc.setMarshaller(springMarshaller);

		boolean doesSupport = jaxbc.canWrite(Sat.class, MediaType.APPLICATION_XML);

		// http://stackoverflow.com/questions/26982466/spring-mvc-response-body-xml-has-extra-string-tags-why

		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
		List<MediaType> mediaTypesList = new ArrayList<>();
		mediaTypesList.add(new MediaType("text", "plain", Charset.forName("UTF-8")));
		mediaTypesList.add(new MediaType("application", "xml", Charset.forName("UTF-8")));
		stringConverter.setSupportedMediaTypes(mediaTypesList);

		converters.add(mc);
		converters.add(mcxml);
		converters.add(jaxbc); // this doesn't work.
		converters.add(stringConverter);

		// super.configureMessageConverters(getMessageConverters());

		// Jackson2ObjectMapperBuilder builder = new
		// Jackson2ObjectMapperBuilder().indentOutput(true)
		// .dateFormat(JsonObjectFiller.getJsonDateFormatter());
		// converters.add(new
		// MappingJackson2HttpMessageConverter(builder.build()));
		// converters.add(new
		// MappingJackson2XmlHttpMessageConverter(Jackson2ObjectMapperBuilder.xml().build()));

	}
}
