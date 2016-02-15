package org.openbox.sf5.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true) // we already defined it
// in xml file
// @ImportResource("/WEB-INF/security-context.xml") // Seems it doesn't work
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	// @Autowired
	// public void configureGlobal(AuthenticationManagerBuilder auth) throws
	// Exception {
	//
	// }
	//
	// @Override
	// protected void configure(HttpSecurity http) throws Exception {
	//
	// // Added UTF-8 encoding
	// //
	// http://stackoverflow.com/questions/20863489/characterencodingfilter-dont-work-together-with-spring-security-3-2-0
	// CharacterEncodingFilter filter = new CharacterEncodingFilter();
	// filter.setEncoding("UTF-8");
	// filter.setForceEncoding(true);
	// http.addFilterBefore(filter, CsrfFilter.class);
	//
	// }

}
