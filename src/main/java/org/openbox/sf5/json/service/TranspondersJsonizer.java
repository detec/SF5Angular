package org.openbox.sf5.json.service;

import java.util.Collections;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.openbox.sf5.common.IniReader;
import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.model.Satellites;
import org.openbox.sf5.model.Transponders;
import org.openbox.sf5.service.CriterionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Helper service class for transponders.
 *
 * @author Andrii Duplyk
 *
 */
@Service
public class TranspondersJsonizer {

	@Autowired
	private IniReader iniReader;

	@Autowired
	private DAO objectsController;

	@Autowired
	private CriterionService criterionService;

	/**
	 *
	 * @param file
	 * @return
	 */
	public Boolean uploadTransponders(MultipartFile file) {
		try {
			iniReader.readMultiPartFile(file);

		} catch (Exception e) {
			return new Boolean(false);
		}

		return new Boolean(iniReader.isResult());

	}

	/**
	 *
	 * @param fieldName
	 * @param typeValue
	 * @return
	 */
	public List<Transponders> getTranspondersByArbitraryFilter(String fieldName, String typeValue) {
		Criterion criterion = criterionService.getCriterionByClassFieldAndStringValue(Transponders.class, fieldName,
				typeValue);

		if (criterion == null) {
			return Collections.emptyList();
		}

		return objectsController.findAllWithRestrictions(Transponders.class, criterion);

	}

	public List<Transponders> getTranspondersBySatelliteId(long satId) {

		Satellites filterSatellite = objectsController.select(Satellites.class, satId);
		if (filterSatellite == null) {
			return Collections.emptyList();
		}
		Criterion criterion = Restrictions.eq("satellite", filterSatellite);

		return objectsController.findAllWithRestrictions(Transponders.class, criterion);

	}

	public CriterionService getCriterionService() {
		return criterionService;
	}

	public void setCriterionService(CriterionService criterionService) {
		this.criterionService = criterionService;
	}

	public DAO getObjectsController() {
		return objectsController;
	}

	public void setObjectsController(DAO objectsController) {
		this.objectsController = objectsController;
	}

}
