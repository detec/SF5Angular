package org.openbox.sf5.json.endpoints;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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
import org.openbox.sf5.model.Transponders;

@RunWith(JUnit4.class)
public class TranspondersServiceIT extends AbstractServiceTest {

	private static final String servicePath = "transponders";

	private Validator validator;

	private long transponderId;

	@Before
	public void setUp() {
		setUpAbstractTestUser();
		serviceTarget = commonTarget.path(servicePath);

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	public void shouldGetTranspondersByArbitraryFilter() {

		Response response = null;

		Invocation.Builder invocationBuilder = serviceTarget.path("filter").path("speed").path("27500")
				.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		response = invocationBuilder.get();

		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		GenericType<List<Transponders>> genList = new GenericType<List<Transponders>>() {
		};

		List<Transponders> newTransList = response.readEntity(genList);

		assertThat(newTransList).isNotNull();
		assertThat(newTransList.size()).isGreaterThan(0);

		validateTranspondersList(newTransList);

		Transponders readTrans = newTransList.get(0);
		transponderId = readTrans.getId();

		invocationBuilder = serviceTarget.path("filter").path("id").path(String.valueOf(transponderId))
				.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

		response = invocationBuilder.get();

		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		Transponders trans = response.readEntity(Transponders.class);

		assertThat(trans).isNotNull();
		Set<ConstraintViolation<Transponders>> constraintViolations = validator.validate(trans);
		assertEquals(0, constraintViolations.size());

	}

	@Test
	@Ignore
	public void shouldGetTranspondersBySatelliteId() {

		Response response = null;

		SatellitesServiceIT satTest = new SatellitesServiceIT();
		satTest.setUp();

		Invocation.Builder invocationBuilder = serviceTarget.path("filter")
				.matrixParam("satId", satTest.getSatelliteId())

				.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		response = invocationBuilder.get();
		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		GenericType<List<Transponders>> genList = new GenericType<List<Transponders>>() {
		};

		List<Transponders> newTransList = response.readEntity(genList);

		assertThat(newTransList).isNotNull();
		assertThat(newTransList.size()).isGreaterThan(0);
	}

	@Test
	public void shouldGetAllTransponders() {

		Response response = null;

		Invocation.Builder invocationBuilder = serviceTarget

				.path("/")

				.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		response = invocationBuilder.get();
		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		GenericType<List<Transponders>> genList = new GenericType<List<Transponders>>() {
		};

		List<Transponders> newTransList = response.readEntity(genList);

		assertThat(newTransList).isNotNull();
		assertThat(newTransList.size()).isGreaterThan(0);
	}

	public void validateTranspondersList(List<Transponders> transList) {
		int[] resultArray = new int[1];

		transList.forEach(t -> {
			Set<ConstraintViolation<Transponders>> constraintViolations = validator.validate(t);
			resultArray[0] = resultArray[0] + constraintViolations.size();

		});

		assertEquals(0, resultArray[0]);
	}

}
