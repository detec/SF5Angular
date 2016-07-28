package org.openbox.sf5.json.endpoints;

import java.util.List;

import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.json.service.TranspondersJsonizer;
import org.openbox.sf5.model.Transponders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@EnableWebMvc
@RequestMapping("${jaxrs.path}/transponders/")
public class TranspondersService {

	@RequestMapping(method = RequestMethod.POST, headers = "content-type=multipart/form-data")
	public ResponseEntity<Boolean> uploadTransponders(@RequestParam("file") MultipartFile file) {

		Boolean result = new Boolean(false);
		if (!file.isEmpty()) {

			result = transpondersJsonizer.uploadTransponders(file);
		} else {
			return new ResponseEntity<>(result, HttpStatus.NOT_IMPLEMENTED);
		}

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "filter/{type}/{typeValue}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Transponders>> getTranspondersByArbitraryFilter(@PathVariable("type") String fieldName,
			@PathVariable("typeValue") String typeValue) {
		List<Transponders> transList = transpondersJsonizer.getTranspondersByArbitraryFilter(fieldName, typeValue);
		if (transList.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(transList, HttpStatus.OK);
	}

	@RequestMapping(value = "filter/id/{transponderId}", method = RequestMethod.GET)
	public ResponseEntity<Transponders> getTransponderById(@PathVariable("transponderId") long tpId) {
		Transponders trans = objectController.select(Transponders.class, tpId);
		if (trans == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<>(trans, HttpStatus.OK);

	}

	@RequestMapping(value = "/{filter}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Transponders>> getTranspondersBySatelliteId(@PathVariable("filter") String ignore,
			@MatrixVariable(required = true, value = "satId") long satId) {

		List<Transponders> transList = transpondersJsonizer.getTranspondersBySatelliteId(satId);
		if (transList.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(transList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Transponders>> getTransponders() {
		List<Transponders> transList = objectController.findAll(Transponders.class);
		if (transList.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(transList, HttpStatus.OK);
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

	@Autowired
	private TranspondersJsonizer transpondersJsonizer;

	@Autowired
	private DAO objectController;

}
