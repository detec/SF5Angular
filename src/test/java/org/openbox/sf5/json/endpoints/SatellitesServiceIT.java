package org.openbox.sf5.json.endpoints;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openbox.sf5.model.Satellites;

@RunWith(JUnit4.class)
public class SatellitesServiceIT extends AbstractServiceTest {

	private String servicePath = "satellites/";

	private long satelliteId;

	@Before
	public void setUp() {
		setUpAbstractTestUser();
		serviceTarget = commonTarget.path(servicePath);
	}

	public long getSatelliteId() {

		Response response = null;
		GenericType<List<Satellites>> genList = new GenericType<List<Satellites>>() {
		};

		Invocation.Builder invocationBuilder = serviceTarget.path("filter").path("name").path("13E")
				.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

		response = invocationBuilder.get();

		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		List<Satellites> satList = invocationBuilder.get(genList);

		assertThat(satList).isNotNull();
		assertThat(satList.size()).isGreaterThan(0);

		Satellites satellite = satList.get(0);
		assertTrue(satellite instanceof Satellites);
		satelliteId = satellite.getId();

		return satelliteId;

	}

	@Ignore
	@Test
	public void shouldgetAllSatellites() {

		Response response = null;
		GenericType<List<Satellites>> genList = new GenericType<List<Satellites>>() {
		};
		Invocation.Builder invocationBuilder = serviceTarget

				// .path("")

				.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		response = invocationBuilder.get();

		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		List<Satellites> satList = invocationBuilder.get(genList);

		assertThat(satList).isNotNull();
		assertThat(satList.size()).isGreaterThan(0);
	}

	@Ignore
	@Test
	public void getSatellitesByArbitraryFilter() {
		Response response = null;
		getSatelliteId();

		Invocation.Builder invocationBuilder = serviceTarget

				.path(String.valueOf(satelliteId)).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		response = invocationBuilder.get();
		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		Satellites satellite = response.readEntity(Satellites.class);
		assertThat(satellite).isNotNull();

	}

}
