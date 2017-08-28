package org.openbox.sf5.json.endpoints;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Settings controller
 *
 * @author Andrii Duplyk
 *
 */
@RestController
@EnableWebMvc
@RequestMapping(value = "${jaxrs.path}/usersettings/")
public class SettingsService {

	private static final String CONSTANT_COULDNT_GET_USER = "Couldn't get currently authenticated user!";

	@Autowired
	private SF5SecurityContext securityContext;

	@Autowired
	private SettingsJsonizer settingsJsonizer;

	@Autowired
	private Jaxb2Marshaller springMarshaller;

	@Value("${jaxrs.path}")
	private String jaxRSPath;

	@PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
	ResponseEntity<Settings> createSetting(@RequestBody Settings setting, UriComponentsBuilder ucBuilder,
			@MatrixVariable(required = false, value = "calculateIntersection") boolean calculateIntersection)
			throws NotAuthenticatedException, UsersDoNotCoincideException, SQLException {

		Users currentUser = getVerifyAuthenticatedUser();

		if (!currentUser.equals(setting.getUser())) {
			// authenticated user and setting user do not coincide.
			throw new UsersDoNotCoincideException("Authenticated user " + currentUser.getId()
					+ " and the one in setting - " + setting.getUser().getId() + " do not coincide!");
		}

		// assigning current date to setting
		setting.setTheLastEntry(new java.sql.Timestamp(System.currentTimeMillis()));

		// Define return status. For existing settings request method is PUT,
		// for new - POST.
		HttpStatus returnStatus = (setting.getId() > 0) ? HttpStatus.OK : HttpStatus.CREATED;

		settingsJsonizer.saveNewSetting(setting, calculateIntersection);

		HttpHeaders headers = new HttpHeaders();
		headers.add("SettingId", Long.toString(setting.getId()));
		// it can be called out of JAX-WS
		try {
			headers.setLocation(ucBuilder.path("/" + jaxRSPath + "/").path("usersettings/{id}")
					.buildAndExpand(setting.getId()).toUri());
		} catch (Exception e) {
			// swallowing.
		}

		return new ResponseEntity<>(setting, headers, returnStatus);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("{settingId}")
	ResponseEntity<Settings> putSetting(@RequestBody Settings setting, UriComponentsBuilder ucBuilder,
			@PathVariable("settingId") long settingId,
			@MatrixVariable(required = false, value = "calculateIntersection") boolean calculateIntersection)
			throws NotAuthenticatedException, UsersDoNotCoincideException, SQLException {

		return createSetting(setting, ucBuilder, calculateIntersection);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("{settingId}")
	ResponseEntity<Settings> deleteSetting(@PathVariable("settingId") long settingId)
			throws NotAuthenticatedException, ItemNotFoundException {
		Users currentUser = getVerifyAuthenticatedUser();

        Optional.ofNullable(settingsJsonizer.getSettingById(settingId, currentUser))
                .orElseThrow(() -> new ItemNotFoundException(String.join("", "Unable to delete. Setting with id ",
                        Long.toString(settingId), " not found for user ", currentUser.getUsername())));

		settingsJsonizer.deleteSetting(settingId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
	public ResponseEntity<List<Settings>> getSettingsByUserLogin() throws NotAuthenticatedException {

		Users currentUser = getVerifyAuthenticatedUser();

		List<Settings> settList = settingsJsonizer.getSettingsByUser(currentUser);
        return settList.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(settList, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("filter/{type}/{typeValue}")
	ResponseEntity<List<Settings>> getSettingsByArbitraryFilter(@PathVariable("type") String fieldName,
			@PathVariable("typeValue") String typeValue) throws NotAuthenticatedException {

		List<Settings> settList;
		Users currentUser = getVerifyAuthenticatedUser();

		settList = settingsJsonizer.getSettingsByArbitraryFilter(fieldName, typeValue, currentUser);
        return settList.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(settList, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("{settingId}")
	ResponseEntity<Settings> getSettingById(@PathVariable("settingId") long settingId)
			throws NotAuthenticatedException {

		Users currentUser = getVerifyAuthenticatedUser();
		Settings setting = settingsJsonizer.getSettingById(settingId, currentUser);
        return setting == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(setting, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "{settingId}/sf5", method = RequestMethod.GET

			, produces = MediaType.TEXT_PLAIN // This removes <String> tags

	)

			ResponseEntity<String>

			// String

			getSettingByIdSF5(@PathVariable("settingId") long settingId) throws NotAuthenticatedException {

		Users currentUser = getVerifyAuthenticatedUser();

		Settings setting = settingsJsonizer.getSettingById(settingId, currentUser);
		if (setting == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		StringWriter sw = new StringWriter();

		List<SettingsConversion> conversionLines = setting.getConversion();
		Sat sat = XMLExporter.exportSettingsConversionPresentationToSF5Format(conversionLines);

		// There is no easy way of using different marshallers
		// marshalling sat
		springMarshaller.marshal(sat, new StreamResult(sw));

		return new ResponseEntity<>(sw.toString(), HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/lines/{lineId}", method = RequestMethod.DELETE)
	ResponseEntity<SettingsConversion> deleteSettingLine(@PathVariable("lineId") long lineId)
			throws NotAuthenticatedException {
		// It is necessary because orphan removal does not work as expected.

		getVerifyAuthenticatedUser();

		settingsJsonizer.deleteSettingLine(lineId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	public SettingsJsonizer getSettingsJsonizer() {
		return settingsJsonizer;
	}

	public void setSettingsJsonizer(SettingsJsonizer settingsJsonizer) {
		this.settingsJsonizer = settingsJsonizer;
	}

	private Users getVerifyAuthenticatedUser() throws NotAuthenticatedException {
        return Optional.ofNullable(securityContext.getCurrentlyAuthenticatedUser())
                .orElseThrow(() -> new NotAuthenticatedException(CONSTANT_COULDNT_GET_USER));
	}
}
