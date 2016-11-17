package org.openbox.sf5.common;

import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;

/**
 *
 * Testing form login authentication.
 *
 * @author Andrii Duplyk
 *
 */
public class CookieLoginTest {

	private static final Logger LOGGER = Logger.getLogger(CookieLoginTest.class.getName());

	private static final String domain = "localhost:8080";

	public static final String appLocation = "http://" + domain;

	public static final String testUsername = "ITUser";

	public static final String testUserPassword = "Test123";

	private static final String cookieName = "JSESSIONID";

	public WebTarget commonTarget;

	public Client client;

	public WebTarget serviceTarget;

	public NewCookie serverCookie;

	public static void main(String[] args) {

		CookieLoginTest instance = new CookieLoginTest();
		instance.authenticateWithCookies(true);

	}

	public Client createAdminClient() {
		return ClientBuilder.newBuilder().register(JacksonJaxbJsonProvider.class)

				.register(JacksonJaxbXMLProvider.class)

				.register(JacksonFeature.class).register(MultiPartFeature.class)

				.register(new LoggingFeature(LOGGER, LoggingFeature.Verbosity.PAYLOAD_ANY))

				.build();
	}

	private void authenticateWithCookies(boolean forAdmin) {
		client = createAdminClient();

		serviceTarget = client.target(appLocation).path("login");
		Invocation.Builder invocationBuilder = serviceTarget.request(MediaType.TEXT_HTML);

		Response response = invocationBuilder.get();

		Form form = new Form();
		form.param("username", (forAdmin) ? "admin" : testUsername);
		form.param("password", (forAdmin) ? "1" : testUserPassword);

		response = serviceTarget.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));

		Map<String, NewCookie> cookies = response.getCookies();
		serverCookie = cookies.get(cookieName);

		// Cookie clientCookie = serverCookie.toCookie();

		Cookie clientCookie = new Cookie(cookieName, serverCookie.getValue(), serverCookie.getPath(), domain,
				serverCookie.getVersion());

		// should get introduction page
		invocationBuilder = client.target(appLocation).request(MediaType.TEXT_HTML).cookie(clientCookie);
		response = invocationBuilder.get();

		String content = response.readEntity(String.class);
		// LOGGER.log(Level.INFO, content);

	}

}
