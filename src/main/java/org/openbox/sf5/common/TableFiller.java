package org.openbox.sf5.common;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.model.CarrierFrequency;
import org.openbox.sf5.model.KindsOfPolarization;
import org.openbox.sf5.model.RangesOfDVB;
import org.openbox.sf5.model.TheDVBRangeValues;
import org.openbox.sf5.model.ValueOfTheCarrierFrequency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Fills necessary tables with settings.
 *
 * @author Andrii Duplyk
 *
 */
@Component
public final class TableFiller {

	private static final String CONSTANAT_POLARIZATION = "polarization";

	private static final String CONSTANT_TYPE_OF_CARRIER_FREQUENCY = "typeOfCarrierFrequency";

	@Autowired
	private DAO objectController;

	public TableFiller() {

	}

	/**
	 * This method executes when context loads.
	 *
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	@EventListener
	public void handleContextRefresh(ContextRefreshedEvent event) {

		List<RangesOfDVB> list = new ArrayList<>();
		list.add(RangesOfDVB.C);
		list.add(RangesOfDVB.KU);

		TheDVBRangeValues newRecord = null;

		Session session = objectController.openSession();

		for (RangesOfDVB e : list) {

			List<TheDVBRangeValues> rec = session.createCriteria(TheDVBRangeValues.class)
					.add(Restrictions.eq("rangeOfDVB", e)).list();

			if (rec.isEmpty()) {

				if (e.equals(RangesOfDVB.C)) {
					newRecord = new TheDVBRangeValues(e, 3400, 4200);

				}

				if (e.equals(RangesOfDVB.KU)) {
					newRecord = new TheDVBRangeValues(e, 10700, 12750);

				}

				objectController.add(newRecord);
			}

		}

		ValueOfTheCarrierFrequency value;
		List<ValueOfTheCarrierFrequency> rec;

		rec = session.createCriteria(ValueOfTheCarrierFrequency.class)
				.add(Restrictions.eq(CONSTANT_TYPE_OF_CARRIER_FREQUENCY, CarrierFrequency.LOWER))
				.add(Restrictions.eq(CONSTANAT_POLARIZATION, KindsOfPolarization.PIE)).list();
		if (rec.isEmpty()) {
			value = new ValueOfTheCarrierFrequency(CarrierFrequency.LOWER, KindsOfPolarization.PIE, 10700, 11699);
			objectController.add(value);
		}

		rec = session.createCriteria(ValueOfTheCarrierFrequency.class)
				.add(Restrictions.eq(CONSTANT_TYPE_OF_CARRIER_FREQUENCY, CarrierFrequency.LOWER))
				.add(Restrictions.eq(CONSTANAT_POLARIZATION, KindsOfPolarization.LINEAR)).list();

		if (rec.isEmpty()) {
			value = new ValueOfTheCarrierFrequency(CarrierFrequency.LOWER, KindsOfPolarization.LINEAR, 10700, 11699);
			objectController.add(value);
		}

		rec = session.createCriteria(ValueOfTheCarrierFrequency.class)
				.add(Restrictions.eq(CONSTANT_TYPE_OF_CARRIER_FREQUENCY, CarrierFrequency.TOP))
				.add(Restrictions.eq(CONSTANAT_POLARIZATION, KindsOfPolarization.LINEAR)).list();

		if (rec.isEmpty()) {
			value = new ValueOfTheCarrierFrequency(CarrierFrequency.TOP, KindsOfPolarization.LINEAR, 11700, 12750);
			objectController.add(value);
		}

		rec = session.createCriteria(ValueOfTheCarrierFrequency.class)
				.add(Restrictions.eq(CONSTANT_TYPE_OF_CARRIER_FREQUENCY, CarrierFrequency.C_RANGE))
				.add(Restrictions.eq(CONSTANAT_POLARIZATION, KindsOfPolarization.LINEAR)).list();

		if (rec.isEmpty()) {
			value = new ValueOfTheCarrierFrequency(CarrierFrequency.C_RANGE, KindsOfPolarization.LINEAR, 3400, 4200);
			objectController.add(value);

		}

		rec = session.createCriteria(ValueOfTheCarrierFrequency.class)
				.add(Restrictions.eq(CONSTANT_TYPE_OF_CARRIER_FREQUENCY, CarrierFrequency.TOP_PIE))
				.add(Restrictions.eq(CONSTANAT_POLARIZATION, KindsOfPolarization.PIE)).list();

		if (rec.isEmpty()) {
			value = new ValueOfTheCarrierFrequency(CarrierFrequency.TOP_PIE, KindsOfPolarization.PIE, 11700, 12750);
			objectController.add(value);

		}

		session.close();

	}

	public DAO getObjectController() {
		return objectController;
	}

	public void setObjectController(DAO objectController) {
		this.objectController = objectController;
	}

}
