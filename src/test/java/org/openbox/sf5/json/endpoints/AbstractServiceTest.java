package org.openbox.sf5.json.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
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

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.BeforeClass;
import org.openbox.sf5.json.service.CustomObjectMapper;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;

public abstract class AbstractServiceTest {

	private static final String cookieName = "JSESSIONID";

	private static final String domain = "localhost:8080";

	public static final String appLocation = "http://" + domain;

	// public static final String jsonPath = "json";

	public static String jsonPath;

	public static final Logger LOGGER = Logger.getLogger(AbstractServiceTest.class.getName());

	public static Cookie clientCookie;

	public CustomObjectMapper mapper = new CustomObjectMapper();

	public XmlMapper xmlMapper;

	public static Client client;

	public WebTarget commonTarget;

	public static WebTarget serviceTarget;

	public static final String testUsername = "ITUser";

	public static final String testUserPassword = "Test123";

	public static Properties property = new Properties();

	public static NewCookie serverCookie;

	// public Client createAdminClient() {
	//
	// mapper = new CustomObjectMapper();
	//
	// xmlMapper = new XmlMapper();
	//
	// return ClientBuilder.newBuilder().register(JacksonJaxbJsonProvider.class)
	//
	// .register(JacksonJaxbXMLProvider.class)
	//
	// .register(JacksonFeature.class).register(MultiPartFeature.class)
	//
	// // .register(new LoggingFilter())
	//
	// .build();
	// }

	// public Client createTestUserClient() {
	// // HttpAuthenticationFeature authenticationFeature =
	// // HttpAuthenticationFeature.digest(testUsername,
	// // testUserPassword);
	//
	// mapper = new CustomObjectMapper();
	//
	// xmlMapper = new XmlMapper();
	// JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
	// jacksonProvider.setMapper(mapper);
	//
	// return ClientBuilder.newBuilder()
	//
	// .register(JacksonJaxbJsonProvider.class)
	//
	// .register(JacksonFeature.class)
	//
	// .register(JacksonJaxbXMLProvider.class)
	//
	// // .register(jacksonProvider)
	//
	// .register(MultiPartFeature.class)
	//
	// // .register(authenticationFeature)
	//
	// // .register(new LoggingFilter())
	//
	// .build();
	// }

	@BeforeClass
	public static void beforeClass() {

		// using try with resources
		try (InputStream in = AbstractServiceTest.class.getResourceAsStream("/application.properties")) {
			property.load(in);
			AbstractServiceTest.jsonPath = property.getProperty("jaxrs.path");

		} catch (IOException e) {
			// put exception into log.
			LOGGER.log(Level.SEVERE, e.getMessage(), e);

		}

		authenticateWithCookies();

	}

	public void setUpAbstractTestUser() {
		// client = createTestUserClient();

		commonTarget = client.target(appLocation)

				// .path(property.getProperty("context.path"))

				.path(jsonPath);
	}

	public void setUpAbstractAdmin() {
		// client = createAdminClient();

		commonTarget = client.target(appLocation).path(property.getProperty("context.path")).path(jsonPath);
	}

	private static void authenticateWithCookies() {
		// client = createAdminClient();

		boolean forAdmin = false;

		client = createClient();

		serviceTarget = client.target(appLocation).path("login");
		Invocation.Builder invocationBuilder = serviceTarget.request(MediaType.TEXT_HTML);

		Response response = invocationBuilder.get();

		Form form = new Form();
		form.param("username", (forAdmin) ? "admin" : testUsername);
		form.param("password", (forAdmin) ? "1" : testUserPassword);

		response = serviceTarget.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));

		Map<String, NewCookie> cookies = response.getCookies();
		serverCookie = cookies.get(cookieName);

		if (serverCookie == null) {
			LOGGER.log(Level.INFO, "Server cookie was not returned!");

			return;
		}

		clientCookie = new Cookie(cookieName, serverCookie.getValue(), serverCookie.getPath(), domain,
				serverCookie.getVersion());

		// should get introduction page
		// invocationBuilder =
		// client.target(appLocation).path("index.html").request(MediaType.TEXT_HTML)
		// .cookie(clientCookie);
		// response = invocationBuilder.get();
		//
		// String content = response.readEntity(String.class);
		// LOGGER.log(Level.INFO, content);

	}

	public static Client createClient() {

		ClientConfig config = new ClientConfig();
		{

			config.register(JacksonJaxbXMLProvider.class).register(JacksonFeature.class)
					.register(MultiPartFeature.class).register(LoggingFeature.class);
		}

		ApacheConnectorProvider provider = new ApacheConnectorProvider();
		{
			config.connectorProvider(new ApacheConnectorProvider());
		}

		Client client = ClientBuilder.newClient(config);

		return client;
	}

}
