package org.openbox.sf5.json.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.openbox.sf5.json.service.CustomObjectMapper;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;

public abstract class AbstractServiceTest {
	public static final String appLocation = "http://localhost:8080";

	// public static final String jsonPath = "json";

	// it doesn't work in abstract class
	// @Value("${jaxrs.path}")
	public static String jsonPath;

	// @Autowired
	public CustomObjectMapper mapper;

	// @Autowired
	public XmlMapper xmlMapper;

	public Client client;

	// public ObjectMapper mapper = new ObjectMapper();

	public WebTarget commonTarget;

	public WebTarget serviceTarget;

	public String testUsername = "ITUser";

	public String testUserPassword = "Test123";

	public Properties property = new Properties();

	public Logger LOGGER = Logger.getLogger(getClass().getName());

	public Client createAdminClient() {

		// https://jersey.java.net/documentation/latest/user-guide.html#d0e5127
		// adding universal, digest and non-preemptive authentication.
		HttpAuthenticationFeature authenticationFeature = HttpAuthenticationFeature.digest("admin", "1");

		mapper = new CustomObjectMapper();

		xmlMapper = new XmlMapper();

		return ClientBuilder.newBuilder().register(JacksonJaxbJsonProvider.class)

				.register(JacksonJaxbXMLProvider.class)

				.register(JacksonFeature.class).register(MultiPartFeature.class).register(authenticationFeature)

				// .register(new LoggingFilter())

				.build();
	}

	public Client createTestUserClient() {
		HttpAuthenticationFeature authenticationFeature = HttpAuthenticationFeature.digest(testUsername,
				testUserPassword);

		mapper = new CustomObjectMapper();

		xmlMapper = new XmlMapper();
		JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
		jacksonProvider.setMapper(mapper);

		return ClientBuilder.newBuilder()

				.register(JacksonJaxbJsonProvider.class)

				.register(JacksonFeature.class)

				. register(JacksonJaxbXMLProvider.class)

				// .register(jacksonProvider)

				.register(MultiPartFeature.class).register(authenticationFeature)

				// .register(new LoggingFilter())

				.build();
	}

	public void loadProperties() {

		// using try with resources
		try (InputStream in = getClass().getResourceAsStream("/application.properties")) {
			property.load(in);
			AbstractServiceTest.jsonPath = property.getProperty("jaxrs.path");

		} catch (IOException e) {
			e.printStackTrace();

			// put exception into log.
			LOGGER.log(Level.SEVERE, e.getMessage(), e);

		}

	}

	public void setUpAbstractTestUser() {
		client = createTestUserClient();
		loadProperties();
		commonTarget = client.target(appLocation).path(property.getProperty("context.path")).path(jsonPath);
	}

	public void setUpAbstractAdmin() {
		client = createAdminClient();
		loadProperties();
		commonTarget = client.target(appLocation).path(property.getProperty("context.path")).path(jsonPath);
	}

}
