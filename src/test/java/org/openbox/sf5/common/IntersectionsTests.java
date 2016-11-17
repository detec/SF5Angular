package org.openbox.sf5.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openbox.sf5.model.Settings;
import org.openbox.sf5.model.SettingsConversion;
import org.openbox.sf5.model.Transponders;
import org.openbox.sf5.model.Users;
import org.openbox.sf5.model.Usersauthorities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

// Taken from http://stackoverflow.com/questions/10385452/location-of-spring-context-xml
@ContextConfiguration(locations = { "file:src/test/resources/context/test-autowired-beans.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class IntersectionsTests extends AbstractJsonizerTest {

	@Autowired
	private TableFillerTests tft;

	@Autowired
	private IniReader iniReader;

	@Autowired
	private Intersections intersections;

	@Before
	public void setUp() {
		super.setUpAbstract();

		// we need 2 to setup catalogues before transponders import
		// we cannot create new object, should use autowiring.
		// TableFillerTests tft = new TableFillerTests();
		tft.setUpAbstract(); // disable logging, formerly used to set up
								// dependencies.

		// TableFiller is a component that is created automatically with
		// context.
		// tft.executeTableFiller();
	}

	@Test
	public void shouldImportTestInis() throws URISyntaxException {

		int positiveResult = 0;
		try {
			positiveResult = getIniImportResult();
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertEquals(3, positiveResult);

	}

	public static Stream<Path> getTransponderFilesStreamPath() throws URISyntaxException, IOException {

		URL transpondersFolderUrl = Thread.currentThread().getContextClassLoader().getResource("transponders/");

		assertThat(transpondersFolderUrl).isNotNull();

		Path path = Paths.get(transpondersFolderUrl.toURI());

		Stream<Path> streamPath = Files.find(path, 2, (newpath, attr) -> String.valueOf(newpath).endsWith(".ini"));

		return streamPath;
	}

	public int getIniImportResult() throws IOException, URISyntaxException {

		List<Boolean> resultList = new ArrayList<>();

		Stream<Path> streamPath = getTransponderFilesStreamPath();

		streamPath.forEach(t -> {
			iniReader.setFilePath(t.toString());
			try {
				iniReader.readData();
				resultList.add(iniReader.isResult());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		streamPath.close();

		int positiveResult = resultList.stream().filter(t -> t.booleanValue()).collect(Collectors.toList()).size();

		return positiveResult;
	}

	@Test
	public void shouldCheckIntersections() throws SQLException {
		Users usr = new Users();
		usr.setUsername("login");
		usr.setPassword("empty");

		List<Usersauthorities> rolesList = new ArrayList<>();

		Usersauthorities checkRoleUser = new Usersauthorities(usr.getUsername(), "ROLE_USER", usr, 2);

		if (!rolesList.contains(checkRoleUser)) {
			rolesList.add(checkRoleUser);
		}

		usr.setAuthorities(rolesList);

		DAO.saveOrUpdate(usr);

		Settings setting = new Settings();
		setting.setName("Intersections test");
		setting.setUser(usr);
		setting.setTheLastEntry(new java.sql.Timestamp(System.currentTimeMillis()));
		DAO.saveOrUpdate(setting);

		List<Transponders> transList = DAO.findAll(Transponders.class);

		// List<SettingsConversion> scList = new ArrayList<>();
		// for (int i = 7; i < 39; i++) {
		// // adding lines to setting
		// SettingsConversion newLine = new SettingsConversion(setting);
		// newLine.setLineNumber(i - 6);
		// newLine.setTransponder(transList.get(i));
		// scList.add(newLine);
		// }

		ConversionLinesHelper.fillTranspondersToSetting(transList, setting);
		List<SettingsConversion> scList = setting.getConversion();

		// if something is wrong - test will fail.
		int rows = intersections.checkIntersection(scList, setting);

		setting.setConversion(scList);
		DAO.saveOrUpdate(setting);
	}

}
