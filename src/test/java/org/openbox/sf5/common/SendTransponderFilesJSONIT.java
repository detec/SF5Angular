package org.openbox.sf5.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.stream.Stream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Before;
import org.junit.Test;
import org.openbox.sf5.json.endpoints.AbstractServiceTest;

public class SendTransponderFilesJSONIT extends AbstractServiceTest {

	private static final String servicePath = "transponders/";

	@Before
	public void setUp() {
		setUpAbstractTestUser();
		serviceTarget = commonTarget.path(servicePath);
	}

	// As for now we cannot cope with programmatic form login authentication.
	// @Ignore
	@Test
	public void shouldSendFileJsonEndpoint() throws URISyntaxException, IOException {
		Stream<Path> transponderFilesPathes = IntersectionsTests.getTransponderFilesStreamPath();

		MediaType contentType1 = MediaType.MULTIPART_FORM_DATA_TYPE;
		// final MediaType contentType = Boundary.addBoundary(contentType1); //
		// import
		// org.glassfish.jersey.media.multipart.Boundary;

		transponderFilesPathes.forEach(t -> sendFile(t, contentType1));
	}

	private void sendFile(Path t, MediaType contentType) {
		FileDataBodyPart filePart = new FileDataBodyPart("file", t.toFile());

		@SuppressWarnings("resource")
		final FormDataMultiPart multipart = (FormDataMultiPart) new FormDataMultiPart().field("foo", "bar")
				.bodyPart(filePart);

		Invocation.Builder invocationBuilder = serviceTarget.request(MediaType.APPLICATION_JSON);

		Response responsePost = invocationBuilder.cookie(clientCookie).post(Entity.entity(multipart, contentType));

		// .post(Entity.entity(filePart, filePart.getMediaType()));

		// assertEquals(200, responsePost.getStatus());

		// Boolean result = responsePost.readEntity(Boolean.class);
		String textResponse = responsePost.readEntity(String.class);

		Boolean result;
		try {
			result = mapper.readValue(textResponse, Boolean.class);
			assertThat(result.booleanValue()).isTrue();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Could not read boolean result", e);
		}

	}

}
