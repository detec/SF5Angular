package org.openbox.sf5.json.endpoints;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.MediaType;

import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.json.service.SatellitesJsonizer;
import org.openbox.sf5.model.Satellites;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Satellites controller
 *
 * @author Andrii Duplyk
 *
 */
@RestController
@EnableWebMvc
@RequestMapping(value = "${jaxrs.path}/satellites/", produces = MediaType.APPLICATION_JSON)
public class SatellitesService {

	@Autowired
	private DAO objectController;

	@Autowired
	private SatellitesJsonizer jsonizer;

    @GetMapping
	ResponseEntity<List<Satellites>> getAllSatellites() {
        List<Satellites> satList = objectController.findAll(Satellites.class);
        return satList.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(satList, HttpStatus.OK);
    }

    @GetMapping("filter/id/{satelliteId}")
	ResponseEntity<Satellites> getSatelliteById(@PathVariable("satelliteId") long satId) {

        return Optional.ofNullable(objectController.select(Satellites.class, satId))
                .map(sat -> new ResponseEntity<>(sat, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
	}

    @GetMapping("filter/{type}/{typeValue}")
	ResponseEntity<List<Satellites>> getSatellitesByArbitraryFilter(@PathVariable("type") String fieldName,
			@PathVariable("typeValue") String typeValue) {

		List<Satellites> satList = jsonizer.getSatellitesByArbitraryFilter(fieldName, typeValue);
        return satList.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(satList, HttpStatus.OK);
	}

	public DAO getObjectController() {
		return objectController;
	}

	public void setObjectController(DAO objectController) {
		this.objectController = objectController;
	}

	public SatellitesJsonizer getJsonizer() {
		return jsonizer;
	}

	public void setJsonizer(SatellitesJsonizer jsonizer) {
		this.jsonizer = jsonizer;
	}

}
