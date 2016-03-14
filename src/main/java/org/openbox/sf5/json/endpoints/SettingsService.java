package org.openbox.sf5.json.endpoints;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.xml.transform.stream.StreamResult;

import org.openbox.sf5.common.SF5SecurityContext;
import org.openbox.sf5.common.XMLExporter;
import org.openbox.sf5.json.exceptions.ItemNotFoundException;
import org.openbox.sf5.json.exceptions.NotAuthenticatedException;
import org.openbox.sf5.json.exceptions.UsersDoNotCoincideException;
import org.openbox.sf5.json.service.SettingsJsonizer;
import org.openbox.sf5.model.Sat;
import org.openbox.sf5.model.Settings;
import org.openbox.sf5.model.SettingsConversion;
import org.openbox.sf5.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

// @EnableWebMvc  // This annotation limits to HEAD, POST, GET
@RestController
// @PreAuthorize("hasRole('ROLE_USER')")
// Be careful not to use annotations produces, consumes - it kicks away
// requests.
@RequestMapping(value = "${jaxrs.path}/usersettings/")
public class SettingsService {

	// http://websystique.com/springmvc/spring-mvc-4-restful-web-services-crud-example-resttemplate/

	// !!!!!! Be careful with annotations in RequestMapping Consumes, Produces
	// !!! For ResponseEntity<List<T>>
	// it should be empty or produces = "application/json".

	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(method = { RequestMethod.POST })
	public ResponseEntity<Settings> createSetting(@RequestBody Settings setting, UriComponentsBuilder ucBuilder,
			@MatrixVariable(required = false, value = "calculateIntersection") boolean calculateIntersection)
			throws NotAuthenticatedException, UsersDoNotCoincideException {
		// System.out.println("Post setting called");
		Users currentUser = securityContext.getCurrentlyAuthenticatedUser();

		if (currentUser == null) {

			// return new ResponseEntity<Settings>(HttpStatus.UNAUTHORIZED);
			throw new NotAuthenticatedException("Couldn't get currently authenticated user!");
		}

		if (!currentUser.equals(setting.getUser())) {
			// authenticated user and setting user do not coincide.
			// return new ResponseEntity<Long>(HttpStatus.NOT_ACCEPTABLE);
			throw new UsersDoNotCoincideException("Authenticated user " + currentUser.getId()
					+ " and the one in setting - " + setting.getUser().getId() + " do not coincide!");
		}

		// assigning current date to setting
		setting.setTheLastEntry(new java.sql.Timestamp(System.currentTimeMillis()));

		// Define return status. For existing settings request method is PUT,
		// for new - POST.
		// HttpStatus returnStatus = (setting.getId() > 0) ? HttpStatus.OK :
		// HttpStatus.CREATED;
		HttpStatus statusResult = null;
		try {
			statusResult = settingsJsonizer.saveNewSetting(setting, calculateIntersection);
		} catch (SQLException se) {
			throw new IllegalStateException("Error when calculating intersection lines", se);
		} catch (Exception e) {
			throw new IllegalStateException("Error when saving new setting", e);
		}
		// if (statusResult.equals(HttpStatus.CONFLICT)) {
		// // return new ResponseEntity<Long>(HttpStatus.CONFLICT);
		// throw new IllegalStateException();
		// }

		HttpHeaders headers = new HttpHeaders();
		headers.add("SettingId", Long.toString(setting.getId()));
		// it can be called out of JAX-WS
		try {
			headers.setLocation(ucBuilder.path("/" + jaxRSPath + "/").path("usersettings/filter/id/{id}")
					.buildAndExpand(setting.getId()).toUri());
		} catch (Exception e) {

		}

		// return new ResponseEntity<Long>(new Long(setting.getId()), headers,
		// HttpStatus.CREATED);
		// Backbone requires return of the whole object to get id.
		// return new ResponseEntity<Settings>(setting, headers,
		// HttpStatus.CREATED);
		return new ResponseEntity<Settings>(setting, headers, statusResult);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "{settingId}", method = RequestMethod.PUT)
	public ResponseEntity<Settings> putSetting(@RequestBody Settings setting, UriComponentsBuilder ucBuilder,
			@PathVariable("settingId") long settingId,
			@MatrixVariable(required = false, value = "calculateIntersection") boolean calculateIntersection)
			throws NotAuthenticatedException, UsersDoNotCoincideException {

		return createSetting(setting, ucBuilder, calculateIntersection);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "{settingId}", method = RequestMethod.DELETE)
	public ResponseEntity<Settings> deleteSetting(@PathVariable("settingId") long settingId)
			throws NotAuthenticatedException, ItemNotFoundException {
		Users currentUser = securityContext.getCurrentlyAuthenticatedUser();
		if (currentUser == null) {

			// return new ResponseEntity<Settings>(HttpStatus.UNAUTHORIZED);
			throw new NotAuthenticatedException("Couldn't get currently authenticated user!");
		}

		Settings setting = settingsJsonizer.getSettingById(settingId, currentUser);
		if (setting == null) {
			// return new ResponseEntity<Settings>(HttpStatus.NO_CONTENT);
			throw new ItemNotFoundException(
					"Unable to delete. Setting with id " + settingId + " not found for user " + currentUser);
		}

		settingsJsonizer.deleteSetting(settingId);
		return new ResponseEntity<Settings>(HttpStatus.NO_CONTENT);
	}

	// @PreAuthorize("hasRole('ROLE_USER')")
	// @RequestMapping(method = RequestMethod.DELETE)
	// public ResponseEntity<Settings> deleteStub() {
	// System.out.println("Delete setting stub accessed!");
	// return new ResponseEntity<Settings>(HttpStatus.NO_CONTENT);
	// }

	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Settings>> getSettingsByUserLogin() throws NotAuthenticatedException {

		Users currentUser = securityContext.getCurrentlyAuthenticatedUser();
		if (currentUser == null) {

			// return new ResponseEntity<Settings>(HttpStatus.UNAUTHORIZED);
			throw new NotAuthenticatedException("Couldn't get currently authenticated user!");
		}

		List<Settings> settList = settingsJsonizer.getSettingsByUser(currentUser);
		if (settList.isEmpty()) {
			return new ResponseEntity<List<Settings>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Settings>>(settList, HttpStatus.OK);

	}

	// http://community.hpe.com/t5/Software-Developers/A-Comprehensive-Example-of-a-Spring-MVC-Application-Part-3/ba-p/6135449

	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "filter/{type}/{typeValue}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Settings>> getSettingsByArbitraryFilter(@PathVariable("type") String fieldName,
			@PathVariable("typeValue") String typeValue) throws NotAuthenticatedException {

		List<Settings> settList = new ArrayList<>();
		Users currentUser = securityContext.getCurrentlyAuthenticatedUser();
		if (currentUser == null) {

			// return new ResponseEntity<Settings>(HttpStatus.UNAUTHORIZED);
			throw new NotAuthenticatedException("Couldn't get currently authenticated user!");
		}

		settList = settingsJsonizer.getSettingsByArbitraryFilter(fieldName, typeValue, currentUser);
		if (settList.isEmpty()) {
			return new ResponseEntity<List<Settings>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Settings>>(settList, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "{settingId}", method = RequestMethod.GET)
	public ResponseEntity<Settings> getSettingById(@PathVariable("settingId") long settingId)
			throws NotAuthenticatedException {

		Users currentUser = securityContext.getCurrentlyAuthenticatedUser();
		if (currentUser == null) {

			// return new ResponseEntity<Settings>(HttpStatus.UNAUTHORIZED);
			throw new NotAuthenticatedException("Couldn't get currently authenticated user!");
		}

		Settings setting = settingsJsonizer.getSettingById(settingId, currentUser);
		if (setting == null) {
			return new ResponseEntity<Settings>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<Settings>(setting, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "{settingId}/sf5", method = RequestMethod.GET

	// , produces = MediaType.APPLICATION_XML
			, produces = MediaType.TEXT_PLAIN // This removes <String> tags

	)
	// @ResponseBody
	public

			ResponseEntity<String>

			// String

			getSettingByIdSF5(@PathVariable("settingId") long settingId) throws NotAuthenticatedException {

		Users currentUser = securityContext.getCurrentlyAuthenticatedUser();
		if (currentUser == null) {

			// return new ResponseEntity<Settings>(HttpStatus.UNAUTHORIZED);
			throw new NotAuthenticatedException("Couldn't get currently authenticated user!");
		}

		Settings setting = settingsJsonizer.getSettingById(settingId, currentUser);
		if (setting == null) {
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
			// return "";
		}

		StringWriter sw = new StringWriter();

		List<SettingsConversion> conversionLines = setting.getConversion();
		Sat sat = XMLExporter.exportSettingsConversionPresentationToSF5Format(conversionLines);

		// There is no easy way of using different marshallers
		// marshalling sat
		springMarshaller.marshal(sat, new StreamResult(sw));

		// return new ResponseEntity<String>(sw.toString(), HttpStatus.OK);
		return new ResponseEntity<String>(sw.toString(), HttpStatus.OK);

		// Cannot fix <String>&lt;sat> and so on
		// return sw.toString();

		// uses Jackson, unfortunately
		// return new ResponseEntity<Sat>(sat, HttpStatus.OK);

		// http://stackoverflow.com/questions/26982466/spring-mvc-response-body-xml-has-extra-string-tags-why
		// useful link
	}

	@Autowired
	private SF5SecurityContext securityContext;

	@Autowired
	private SettingsJsonizer settingsJsonizer;

	public SettingsJsonizer getSettingsJsonizer() {
		return settingsJsonizer;
	}

	public void setSettingsJsonizer(SettingsJsonizer settingsJsonizer) {
		this.settingsJsonizer = settingsJsonizer;
	}

	@Autowired
	public Jaxb2Marshaller springMarshaller;

	@Value("${jaxrs.path}")
	private String jaxRSPath;

}
