package org.openbox.sf5.common;

import java.util.logging.Logger;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Scope("request")
public class LoginController {

	// It probably uses
	// http://websystique.com/spring-security/spring-security-4-custom-login-form-annotation-example/

	private Logger logger = Logger.getLogger(LoginController.class.getName());

	@RequestMapping(value = "/login", method = { RequestMethod.GET })
	public String login(@RequestParam(value = "error", required = false) boolean loginError, Model model) {

		if (loginError) {
			// indicate about bad credentials.
			model.addAttribute("errormessage", "Bad username/password!");
		}
		return "login";
	}

	@RequestMapping(value = "/login", method = { RequestMethod.PUT, RequestMethod.DELETE })
	public String otherMethodsLogin() {
		logger.info("Redirect to /login with PUT, DELETE");
		return "login";

	}

}
