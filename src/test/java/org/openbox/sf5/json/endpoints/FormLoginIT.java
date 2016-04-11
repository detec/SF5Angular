package org.openbox.sf5.json.endpoints;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Locale;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;

public class FormLoginIT extends AbstractServiceTest {

	private static final String servicePath = "login";

	@Before
	public void setUp() {
		// setUpAbstractTestUser();
		client = createTestUserClient();
		commonTarget = client.target(appLocation);
		serviceTarget = commonTarget.path(servicePath);
	}

	// @Test
	public void shouldAuthenticateFormLogin() {

		Invocation.Builder invocationBuilder = serviceTarget.request(MediaType.TEXT_HTML);

		// Here we should accept cookie.
		Response response = invocationBuilder.get();
		Map<String, NewCookie> cookies = response.getCookies();
		cookie = cookies.get("JSESSIONID");

		// System.out.println("Name " + cookie.getName());
		// System.out.println("Path " + cookie.getPath());
		// System.out.println("Value " + cookie.getValue());
		// System.out.println("Expiry " + cookie.getExpiry());
		// System.out.println("Version " + cookie.getVersion());
		// System.out.println("Max age in secs " + cookie.getMaxAge());

		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertThat(cookie).isNotNull();

		// http://stackoverflow.com/questions/2136119/using-the-jersey-client-to-do-a-post-operation
		Form form = new Form();
		form.param("username", "admin");
		form.param("password", "1");

		// Entity<Form> entity = Entity.form(form);

		// String textResponse =
		// serviceTarget.request().post(Entity.entity(form,
		// MediaType.APPLICATION_FORM_URLENCODED),
		// String.class);

		NewCookie cleanedCookie = new NewCookie(cookie.getName(), cookie.getValue());

		CacheControl cachControl = new CacheControl();
		cachControl.setNoCache(true);

		invocationBuilder = serviceTarget.request(MediaType.TEXT_HTML).accept(MediaType.APPLICATION_XHTML_XML)
				.cacheControl(cachControl)
				.header("User-Agent", "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)")
				.acceptEncoding(new String[] { "gzip", "deflate" }).acceptLanguage(new Locale("ru-RU"))
				.cookie(cleanedCookie);
		// String textResponse = invocationBuilder.post(Entity.entity(form,
		// MediaType.APPLICATION_FORM_URLENCODED),
		// String.class);

		response = invocationBuilder.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED), Response.class);

		String textResponse = response.readEntity(String.class);

		// http://mjremijan.blogspot.com/2015/12/glassfish-jersey-jax-rs-rest-service.html

		System.out.printf("Response: %s%n%n", response);
		System.out.printf("AllowdMethods: %s%n%n", response.getAllowedMethods());
		System.out.printf("Class: %s%n%n", response.getClass());
		System.out.printf("Cookies: %s%n%n", response.getCookies());
		System.out.printf("Date: %s%n%n", response.getDate());
		System.out.printf("Entity: %s%n%n", response.getEntity());
		System.out.printf("EntityTag: %s%n%n", response.getEntityTag());
		System.out.printf("Headers: %s%n%n", response.getHeaders());
		System.out.printf("Language: %s%n%n", response.getLanguage());
		System.out.printf("LastModified: %s%n%n", response.getLastModified());
		System.out.printf("Length: %s%n%n", response.getLength());
		System.out.printf("Links: %s%n%n", response.getLinks());
		System.out.printf("Location: %s%n%n", response.getLocation());
		System.out.printf("MediaType: %s%n%n", response.getMediaType());
		System.out.printf("Metadata: %s%n%n", response.getMetadata());
		System.out.printf("Status: %s%n%n", response.getStatus());
		System.out.printf("StatusInfo: %s%n%n", response.getStatusInfo());
		System.out.printf("StringHeaders: %s%n%n", response.getStringHeaders());
		System.out.printf("hasEntity: %b%n%n", response.hasEntity());
		System.out.printf("readEntity(String): %s%n%n", textResponse);

		// assertThat(textResponse).isNotEmpty();

		// response = serviceTarget.request(MediaType.APPLICATION_JSON_TYPE)
		// .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));

		// Map<String, NewCookie> cookies = response.getCookies();

		assertThat(textResponse).isNotNull();
	}

}
