package org.openbox.sf5.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan({ "org.openbox.sf5.*" })
@Import({
		// MvcConfiguration.class

		// let's exclude it from the root config
		// after excluding ManualWebMvcConfiguration even index.html stops
		// resolving.
		ManualWebMvcConfiguration.class

		,

		SecurityConfiguration.class

})
@ImportResource({ "/WEB-INF/root-context.xml"

		, "/WEB-INF/springWebMultipartContext.xml"

		, "/WEB-INF/security-context.xml"

})
public class AppConfiguration {

	// All taken from the great tutorial
	// http://codehustler.org/blog/spring-security-tutorial-form-login-java-config/

}
