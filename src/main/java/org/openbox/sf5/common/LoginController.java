package org.openbox.sf5.common;

import javax.servlet.http.HttpServletRequest;

import org.openbox.sf5.model.UserDto;
import org.openbox.sf5.model.Users;
import org.openbox.sf5.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

@Controller
@Scope("request")
public class LoginController {

	// It probably uses
	// http://websystique.com/spring-security/spring-security-4-custom-login-form-annotation-example/

	@Autowired
	// @Qualifier("userService")
	private IUserService userService;

	@Autowired
	@Qualifier("authenticationManager")
	protected AuthenticationManager authenticationManager;

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String showRegistrationForm(WebRequest request, Model model) {
		UserDto userDto = new UserDto();
		model.addAttribute("user", userDto);
		// return "register";

		return "register.html";
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String registerUserAccount(@ModelAttribute("user") UserDto accountDto, BindingResult result,
			HttpServletRequest request, Errors errors, Model model) {

		// Let's manually check if password and other fields are empty
		if (accountDto.getUsername().equals("")) {
			model.addAttribute("viewErrMsg", "Field 'Username' cannot be empty!");
			return "login";
		}

		if (accountDto.getPassword().equals("")) {
			model.addAttribute("viewErrMsg", "Field 'Password' cannot be empty!");
			// return new ModelAndView("register",
			return "login";
		}

		if (accountDto.getMatchingPassword().equals("")) {
			model.addAttribute("viewErrMsg", "Field 'Matching password' cannot be empty!");
			return "login";
		}

		if (!accountDto.getPassword().equals(accountDto.getMatchingPassword())) {
			model.addAttribute("viewErrMsg", "Passwords do not match!");
			return "login";
		}

		Users user = new Users();
		if (!result.hasErrors()) {
			user = createUserAccount(accountDto, result);
		}
		if (user == null) {

			model.addAttribute("viewErrMsg", "User not created! There is a user with such name!");
			return "login";
		}

		if (result.hasErrors()) {

			model.addAttribute("viewErrMsg", "Unknown error!");
			return "login";
		} else {

			// I added this from stackoverflow example
			authenticateUserAndSetSession(user, request);

			model.addAttribute("username", user.getusername());
			model.addAttribute("viewMsg", user.getusername() + " successfully registered!");
			return "login";
		}

	}

	private Users createUserAccount(UserDto accountDto, BindingResult result) {
		Users registered = null;
		try {
			registered = userService.registerNewUserAccount(accountDto);
		} catch (Exception e) {
			return null;
		}
		return registered;
	}

	private void authenticateUserAndSetSession(Users user, HttpServletRequest request) {
		String username = user.getusername();
		String password = user.getPassword();
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

		// generate session if one doesn't exist
		request.getSession();

		token.setDetails(new WebAuthenticationDetails(request));
		Authentication authenticatedUser = authenticationManager.authenticate(token);

		SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
	}

	// It probably uses
	// http://websystique.com/spring-security/spring-security-4-custom-login-form-annotation-example/

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(@RequestParam(value = "error", required = false) boolean loginError, Model model) {

		if (loginError) {
			// indicate about bad credentials.
			model.addAttribute("errormessage", "Bad username/password!");
		}

		UserDto userDto = new UserDto();
		model.addAttribute("user", userDto);

		return "login";
	}

	// http://stackoverflow.com/questions/8781558/neither-bindingresult-nor-plain-target-object-for-bean-name-available-as-request
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String displayLogin(Model model) {

		UserDto userDto = new UserDto();
		model.addAttribute("user", userDto);

		return "login";
	}

}
