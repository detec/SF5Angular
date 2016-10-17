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

/**
 * Login is better performed with Spring MVC
 *
 * @author Andrii Duplyk
 *
 */
@Controller
@Scope("request")
public class LoginController {

	// It probably uses
	// http://websystique.com/spring-security/spring-security-4-custom-login-form-annotation-example/

	private static final String CONSTANT_LOGIN = "login";

	private static final String CONSTANT_VIEW_ERROR = "viewErrMsg";

	@Autowired
	private IUserService userService;

	@Autowired
	@Qualifier("authenticationManager")
	protected AuthenticationManager authenticationManager;

	/**
	 * register endpoint
	 *
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String showRegistrationForm(WebRequest request, Model model) {
		UserDto userDto = new UserDto();
		model.addAttribute("user", userDto);
		return "register.html";
	}

	/**
	 * register endpoint
	 *
	 * @param accountDto
	 * @param result
	 * @param request
	 * @param errors
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String registerUserAccount(@ModelAttribute("user") UserDto accountDto, BindingResult result,
			HttpServletRequest request, Errors errors, Model model) {

		// Let's manually check if password and other fields are empty
		if (accountDto.getUsername().isEmpty()) {
			model.addAttribute(CONSTANT_VIEW_ERROR, "Field 'Username' cannot be empty!");
			return CONSTANT_LOGIN;
		}

		if (accountDto.getPassword().isEmpty()) {
			model.addAttribute(CONSTANT_VIEW_ERROR, "Field 'Password' cannot be empty!");
			return CONSTANT_LOGIN;
		}

		if (accountDto.getMatchingPassword().isEmpty()) {
			model.addAttribute(CONSTANT_VIEW_ERROR, "Field 'Matching password' cannot be empty!");
			return CONSTANT_LOGIN;
		}

		if (!accountDto.getPassword().equals(accountDto.getMatchingPassword())) {
			model.addAttribute(CONSTANT_VIEW_ERROR, "Passwords do not match!");
			return CONSTANT_LOGIN;
		}

		Users user = new Users();
		if (!result.hasErrors()) {
			user = createUserAccount(accountDto);
		}
		if (user == null) {

			model.addAttribute(CONSTANT_VIEW_ERROR, "User not created! There is a user with such name!");
			return CONSTANT_LOGIN;
		}

		if (result.hasErrors()) {

			model.addAttribute(CONSTANT_VIEW_ERROR, "Unknown error!");
			return CONSTANT_LOGIN;
		} else {

			// I added this from stackoverflow example
			authenticateUserAndSetSession(user, request);

			model.addAttribute("username", user.getusername());
			model.addAttribute("viewMsg", user.getusername() + " successfully registered!");
			// Let's redirect to html page
			return "redirect:/index.html";

		}

	}

	private Users createUserAccount(UserDto accountDto) {
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

	/**
	 * login endpoint
	 *
	 * @param loginError
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(@RequestParam(value = "error", required = false) boolean loginError, Model model) {

		if (loginError) {
			// indicate about bad credentials.
			model.addAttribute("errormessage", "Bad username/password!");
		}

		UserDto userDto = new UserDto();
		model.addAttribute("user", userDto);

		return CONSTANT_LOGIN;
	}

}
