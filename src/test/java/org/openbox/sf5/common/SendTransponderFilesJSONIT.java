package org.openbox.sf5.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
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

	private static final String servicePath = "transponders";

	@Before
	public void setUp() {
		setUpAbstractTestUser();
		serviceTarget = commonTarget.path(servicePath);
	}

	@Test
	public void shouldSendFileJsonEndpoint() throws URISyntaxException, IOException {
		Stream<Path> transponderFilesPathes = IntersectionsTests.getTransponderFilesStreamPath();

		transponderFilesPathes.forEach(t -> {

			FileDataBodyPart filePart = new FileDataBodyPart("file", t.toFile());

			@SuppressWarnings("resource")
			final FormDataMultiPart multipart = (FormDataMultiPart) new FormDataMultiPart().field("foo", "bar")
					.bodyPart(filePart);

			Invocation.Builder invocationBuilder = serviceTarget.request(MediaType.APPLICATION_JSON);

			Response responsePost = invocationBuilder.post(Entity.entity(multipart, multipart.getMediaType()));
			Boolean result = responsePost.readEntity(Boolean.class);
			assertThat(result.booleanValue()).isTrue();

		});
	}

}
