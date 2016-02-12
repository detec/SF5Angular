package org.openbox.sf5.json.service;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.openbox.sf5.common.JsonObjectFiller;
import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.model.Satellites;
import org.openbox.sf5.service.CriterionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SatellitesJsonizer implements Serializable {

	public String getSatellitesList() {
		List<Satellites> satList = listService.ObjectsList(Satellites.class);
		String result = JsonObjectFiller.getJsonFromObjectsList(satList);

		return result;
	}

	public List<Satellites> getSatellitesByArbitraryFilter(String fieldName, String typeValue) {

		Criterion criterion = criterionService.getCriterionByClassFieldAndStringValue(Satellites.class, fieldName,
				typeValue);
		List<Satellites> satList = listService.ObjectsCriterionList(Satellites.class, criterion);

		return satList;
	}

	public CriterionService getCriterionService() {
		return criterionService;
	}

	public void setCriterionService(CriterionService criterionService) {
		this.criterionService = criterionService;
	}

	@Autowired
	private DAO listService;

	public DAO getListService() {
		return listService;
	}

	public void setListService(DAO listService) {
		this.listService = listService;
	}

	private static final long serialVersionUID = 3401682206534536724L;

	@Autowired
	private CriterionService criterionService;

}
