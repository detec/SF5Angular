package org.openbox.sf5.json.endpoints;

import java.util.List;
import java.util.Optional;

import javax.servlet.annotation.MultipartConfig;
import javax.ws.rs.core.MediaType;

import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.json.service.TranspondersJsonizer;
import org.openbox.sf5.model.Transponders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Transponders controller
 *
 * @author Andrii Duplyk
 *
 */
@RestController
@EnableWebMvc
@RequestMapping("${jaxrs.path}/transponders/")
@MultipartConfig(fileSizeThreshold = 5242880, maxFileSize = 5242880, // 5
		// MB
		maxRequestSize = 20971520) // 20 MB
public class TranspondersService {

    public static final String FILTER_TYPE_PATTERN = "filter/{type}/{typeValue}";

	@Autowired
	private TranspondersJsonizer transpondersJsonizer;

	@Autowired
	private DAO objectController;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA)
	ResponseEntity<Boolean> uploadTransponders(@RequestParam("file") MultipartFile file) {

		Boolean result = new Boolean(false);
		if (!file.isEmpty()) {

			result = transpondersJsonizer.uploadTransponders(file);
		} else {
			return new ResponseEntity<>(result, HttpStatus.NOT_IMPLEMENTED);
		}

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

    @GetMapping(value = FILTER_TYPE_PATTERN, produces = MediaType.APPLICATION_JSON)
	ResponseEntity<List<Transponders>> getTranspondersByArbitraryFilter(@PathVariable("type") String fieldName,
			@PathVariable("typeValue") String typeValue) {
		List<Transponders> transList = transpondersJsonizer.getTranspondersByArbitraryFilter(fieldName, typeValue);
        return transList.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(transList, HttpStatus.OK);
	}

    @GetMapping(value = "/filter/id/{transponderId}")
	ResponseEntity<Transponders> getTransponderById(@PathVariable("transponderId") long tpId) {
        return Optional.ofNullable(objectController.select(Transponders.class, tpId))
                .map(trans -> new ResponseEntity<>(trans, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
	}

    @GetMapping(value = "/{filter}", produces = MediaType.APPLICATION_JSON)
	ResponseEntity<List<Transponders>> getTranspondersBySatelliteId(@PathVariable("filter") String ignore,
			@MatrixVariable(required = true, value = "satId") long satId) {

		List<Transponders> transList = transpondersJsonizer.getTranspondersBySatelliteId(satId);
        return transList.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(transList, HttpStatus.OK);
	}

    @GetMapping(produces = MediaType.APPLICATION_JSON)
	ResponseEntity<List<Transponders>> getTransponders() {
		List<Transponders> transList = objectController.findAll(Transponders.class);
        return transList.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(transList, HttpStatus.OK);
	}

	public DAO getObjectController() {
		return objectController;
	}

	public void setObjectController(DAO objectController) {
		this.objectController = objectController;
	}

	public TranspondersJsonizer getTranspondersJsonizer() {
		return transpondersJsonizer;
	}

	public void setTranspondersJsonizer(TranspondersJsonizer transpondersJsonizer) {
		this.transpondersJsonizer = transpondersJsonizer;
	}
}
