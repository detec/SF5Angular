package org.openbox.sf5.json.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.openbox.sf5.model.Settings;
import org.openbox.sf5.model.SettingsConversion;
import org.openbox.sf5.model.Transponders;
import org.openbox.sf5.model.Users;

public class BuildTestSetting {

	public static Settings buildSetting(Users adminUser, List<Transponders> newTransList, String settingName) {

		Settings setting = new Settings();
		setting.setName(settingName);
		setting.setUser(adminUser);
		setting.setTheLastEntry(new java.sql.Timestamp(System.currentTimeMillis()));

		List<SettingsConversion> scList = new ArrayList<>();

		// filter up to 32 transponders
		newTransList.stream().filter(t -> newTransList.indexOf(t) <= 31).forEach(t -> {
			int currentIndex = newTransList.indexOf(t);
			int currentNumber = currentIndex + 1;
			int satIndex = (int) Math.ceil((double) currentNumber / 4);
			// int tpIndex = currentNumber - (satIndex * 4);
			int tpIndex = (currentNumber % 4 == 0) ? 4 : currentNumber % 4; // %
			// is
			// remainder

			SettingsConversion sc = new SettingsConversion(setting, t, satIndex, tpIndex,
					Long.toString(t.getFrequency()), 0);
			sc.setLineNumber(currentNumber);

			scList.add(sc);

		});

		setting.setConversion(scList);

		return setting;

	}

	public static void checkCreatedSetting(Response responsePost, Settings setting) {
		assertEquals(Status.CREATED.getStatusCode(), responsePost.getStatus());

		// get setting id
		MultivaluedMap<String, String> headersMap = responsePost.getStringHeaders();
		List<String> locStringList = headersMap.get("SettingId");
		assertEquals(1, locStringList.size());

		String settingIdString = locStringList.get(0);
		long id = Long.parseLong(settingIdString);

		assertThat(id).isGreaterThan(0);
		setting.setId(id);
	}
}
