package org.openbox.sf5.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.client.WebTarget;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.junit.Before;
import org.junit.Test;
import org.openbox.sf5.json.endpoints.AbstractServiceTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class SendTransponderFilesJSONIT extends AbstractServiceTest {

	private static final String servicePath = "transponders/";

	private List<String> cookies;

	private RestTemplate restTemplate = new RestTemplate();

	@Before
	public void setUp() {
		// setUpAbstractTestUser();

		commonTarget = client.target(appLocation)

				.path(property.getProperty("context.path"))

				.path(jsonPath);
		serviceTarget = commonTarget.path(servicePath);

		WebTarget loginTarget = client.target(appLocation).path(property.getProperty("context.path"))

				.path("login");
		URI loginUri = loginTarget.getUri();

		// final HttpComponentsClientHttpRequestFactory factory = new
		// HttpComponentsClientHttpRequestFactory();
		// final HttpClient httpClient =
		// HttpClientBuilder.create().setRedirectStrategy(new
		// LaxRedirectStrategy()).build();
		// factory.setHttpClient(httpClient);
		// restTemplate.setRequestFactory(factory);

		// Credentials cred = new UsernamePasswordCredentials(testUsername,
		// testUserPassword);
		//
		// HttpEntity<Credentials> request = new HttpEntity<>(cred);

		// Form form = new Form();
		// form.param("username", testUsername);
		// form.param("password", testUserPassword);
		// HttpEntity<Form> request = new HttpEntity<>(form);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
		form.add("username", testUsername);
		form.add("password", testUserPassword);
		HttpEntity<Object> request = new HttpEntity<>(form, headers);

		HttpEntity<String> response = restTemplate.exchange(loginUri, HttpMethod.POST, request, String.class);

		// System.out.println(response.getBody());

		// response.getHeaders();
		cookies = response.getHeaders().get("Set-Cookie");

		int a = 1;

		// we need to follow redirect to validate session.
		final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		final HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
		factory.setHttpClient(httpClient);
		restTemplate.setRequestFactory(factory);

		// headers = getCookiesHeaders();
		// HttpEntity<String> requestEntity = new HttpEntity<>("parameters",
		// headers);
		//
		// response = restTemplate.exchange(appLocation + "index.html",
		// HttpMethod.GET, requestEntity, String.class);
		// String body = response.getBody();

	}

	// As for now we cannot cope with programmatic form login authentication.
	// @Ignore
	@Test
	public void shouldSendFileJsonEndpoint() throws URISyntaxException, IOException {
		Stream<Path> transponderFilesPathes = IntersectionsTests.getTransponderFilesStreamPath();

		// MediaType contentType1 = MediaType.MULTIPART_FORM_DATA_TYPE;
		// final MediaType contentType = Boundary.addBoundary(contentType1); //
		// import
		// org.glassfish.jersey.media.multipart.Boundary;

		transponderFilesPathes.forEach(t -> sendFile(t));
	}

	private void sendFile(Path t) {
		// FileDataBodyPart filePart = new FileDataBodyPart("file", t.toFile());
		//
		// @SuppressWarnings("resource")
		// final FormDataMultiPart multipart = (FormDataMultiPart) new
		// FormDataMultiPart().field("foo", "bar")
		// .bodyPart(filePart);
		//
		// Invocation.Builder invocationBuilder =
		// serviceTarget.request(MediaType.APPLICATION_JSON);
		//
		// Response responsePost =
		// invocationBuilder.cookie(clientCookie).post(Entity.entity(multipart,
		// contentType));
		//
		//
		// assertEquals(200, responsePost.getStatus());

		// RestTemplate restTemplate = new RestTemplate();

		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

		map.add("file", new FileSystemResource(t.toFile()));

		HttpHeaders headers = getCookiesHeaders();

		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		List<MediaType> acceptTypes = new ArrayList<>();
		acceptTypes.add(MediaType.APPLICATION_JSON);
		headers.setAccept(acceptTypes);

		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
		ResponseEntity<String> stringResponse = restTemplate.exchange(serviceTarget.getUri(), HttpMethod.POST,
				requestEntity, String.class);

		HttpStatus statusCode = stringResponse.getStatusCode();

		assertEquals(HttpStatus.OK, statusCode);

		String body = stringResponse.getBody();

		// Boolean result = responsePost.readEntity(Boolean.class);
		// String textResponse = responsePost.readEntity(String.class);

		Boolean result;
		try {
			result = mapper.readValue(body, Boolean.class);
			assertThat(result.booleanValue()).isTrue();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Could not read boolean result", e);
		}

	}

	private HttpHeaders getCookiesHeaders() {
		HttpHeaders headers = new HttpHeaders();

		headers.set("Cookie", cookies.stream().limit(1).collect(Collectors.joining(";")));

		return headers;
	}

}
