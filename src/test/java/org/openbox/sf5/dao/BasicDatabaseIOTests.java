package org.openbox.sf5.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openbox.sf5.model.CarrierFrequency;
import org.openbox.sf5.model.DVBStandards;
import org.openbox.sf5.model.Polarization;
import org.openbox.sf5.model.RangesOfDVB;
import org.openbox.sf5.model.Satellites;
import org.openbox.sf5.model.Settings;
import org.openbox.sf5.model.SettingsConversion;
import org.openbox.sf5.model.Transponders;
import org.openbox.sf5.model.TypesOfFEC;
import org.openbox.sf5.model.Users;
import org.openbox.sf5.model.Usersauthorities;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = { "file:src/test/resources/context/test-autowired-beans.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class BasicDatabaseIOTests extends org.openbox.sf5.common.AbstractJsonizerTest {

	@Before
	public void setUp() {
		super.setUpAbstract();
	}

	@Test
	@Transactional
	public void shouldInsertSatellite() {

		Satellites newSat = getNewSatellite();
		DAO.saveOrUpdate(newSat);
		assertThat(newSat.getId()).isNotEqualTo(0);
	}

	@Test
	@Transactional
	public void shouldInsertTransponder() {
		Transponders trans = getNewTransponder();
		DAO.saveOrUpdate(trans);
		assertThat(trans.getId()).isNotEqualTo(0);

	}

	@Test
	@Transactional
	public void shouldInsertSetting() {
		Settings setting = getNewSetting();
		DAO.saveOrUpdate(setting);
		assertThat(setting.getId()).isNotEqualTo(0);
	}

	private Satellites getNewSatellite() {
		Satellites newSat = new Satellites();
		newSat.setName("Test sat");

		return newSat;
	}

	private Transponders getNewTransponder() {
		Satellites newSat = getNewSatellite();
		DAO.saveOrUpdate(newSat);

		Transponders trans = new Transponders();
		trans.setCarrier(CarrierFrequency.TOP);
		trans.setFEC(TypesOfFEC._23);
		trans.setFrequency(11555);
		trans.setPolarization(Polarization.V);
		trans.setRangeOfDVB(RangesOfDVB.KU);
		trans.setSatellite(newSat);
		trans.setSpeed(10000);
		trans.setVersionOfTheDVB(DVBStandards.DVBS2);

		return trans;
	}

	private SettingsConversion getNewSettingsConversionLine() {
		Transponders trans = getNewTransponder();
		trans.setSpeed(10500);
		DAO.saveOrUpdate(trans);

		SettingsConversion sc = new SettingsConversion();
		sc.setLineNumber(1);
		sc.setNote("First");
		sc.setSatindex(1);
		sc.setTpindex(2);
		sc.setTransponder(trans);

		return sc;
	}

	private Users getNewUser() {
		Users user = new Users();
		user.setenabled(true);
		user.setPassword("1");
		user.setusername("testuser");

		List<Usersauthorities> rolesList = new ArrayList<>();

		Usersauthorities checkRoleUser = new Usersauthorities(user.getusername(), "ROLE_USER", user, 2);

		if (!rolesList.contains(checkRoleUser)) {
			rolesList.add(checkRoleUser);
		}
		user.setauthorities(rolesList);

		return user;
	}

	private Settings getNewSetting() {
		Users user = getNewUser();
		DAO.saveOrUpdate(user);

		Settings setting = new Settings();
		setting.setName("Test");
		setting.setUser(user);
		setting.setTheLastEntry(new java.sql.Timestamp(System.currentTimeMillis()));

		List<SettingsConversion> scList = new ArrayList<>();

		SettingsConversion sc1 = getNewSettingsConversionLine();
		sc1.setLineNumber(1);
		sc1.setParentId(setting);

		SettingsConversion sc2 = getNewSettingsConversionLine();
		sc2.setLineNumber(2);
		sc2.setParentId(setting);
		sc2.setNote("Useful");

		scList.add(sc1);
		scList.add(sc2);
		setting.setConversion(scList);

		return setting;
	}
}
