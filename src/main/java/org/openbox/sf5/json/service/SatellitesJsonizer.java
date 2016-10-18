package org.openbox.sf5.json.service;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.model.Satellites;
import org.openbox.sf5.service.CriterionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Helper service class for settings.
 * 
 * @author Andrii Duplyk
 *
 */
@Service
public class SatellitesJsonizer {

	@Autowired
	private CriterionService criterionService;

	@Autowired
	private DAO listService;

	/**
	 *
	 * @param fieldName
	 * @param typeValue
	 * @return
	 */
	public List<Satellites> getSatellitesByArbitraryFilter(String fieldName, String typeValue) {

		Criterion criterion = criterionService.getCriterionByClassFieldAndStringValue(Satellites.class, fieldName,
				typeValue);
		return listService.findAllWithRestrictions(Satellites.class, criterion);

	}

	public CriterionService getCriterionService() {
		return criterionService;
	}

	public void setCriterionService(CriterionService criterionService) {
		this.criterionService = criterionService;
	}

	public DAO getListService() {
		return listService;
	}

	public void setListService(DAO listService) {
		this.listService = listService;
	}

}
