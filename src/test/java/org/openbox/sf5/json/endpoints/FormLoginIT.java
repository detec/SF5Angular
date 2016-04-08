package org.openbox.sf5.json.endpoints;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
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
		setUpAbstractTestUser();
		commonTarget = client.target(appLocation);
		serviceTarget = commonTarget.path(servicePath);
	}

	// @Test
	public void shouldAuthenticateFormLogin() {

		Invocation.Builder invocationBuilder = serviceTarget.request(MediaType.APPLICATION_JSON);

		Response response = invocationBuilder.get();

		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		// http://stackoverflow.com/questions/2136119/using-the-jersey-client-to-do-a-post-operation
		Form form = new Form();
		form.param("username", "admin");
		form.param("password", "1");

		Entity<Form> entity = Entity.form(form);

		String textResponse = serviceTarget.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED),
				String.class);
		//
		// assertThat(textResponse).isNotEmpty();

		// response = serviceTarget.request(MediaType.APPLICATION_JSON_TYPE)
		// .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));

		Map<String, NewCookie> cookies = response.getCookies();

		assertThat(response).isNotNull();
	}

}
