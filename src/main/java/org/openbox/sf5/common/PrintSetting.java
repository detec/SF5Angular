package org.openbox.sf5.common;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.model.Settings;
import org.openbox.sf5.model.SettingsConversion;
import org.openbox.sf5.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Class to present setting in printed form. Better done with Spring MVC.
 *
 * @author Andrii Duplyk
 *
 */
@Controller
@PreAuthorize("hasRole('ROLE_USER')")
@Scope("request")
public class PrintSetting {

	@Autowired
	private DAO objectsController;

	private long id;

	private Timestamp theLastEntry;

	private Settings settingsObject;

	private Users user;

	private String name;

	private List<SettingsConversion> dataSettingsConversion = new ArrayList<>();

	/**
	 * print endpoint
	 *
	 * @param pid
	 * @param model
	 * @param session
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/print", method = RequestMethod.GET)
	public String printSetting(@RequestParam(value = "id", required = true) long pid, Model model,
			HttpSession session) {

		readAndFillBeanfromSetting(pid);

		renumerateLines();

		XMLExporter.generateSatTp(dataSettingsConversion);

		model.addAttribute("bean", this);
		model.addAttribute("sessiondate", new Date(session.getLastAccessedTime()));
		return "settingprintfull";
	}

	public DAO getObjectsController() {
		return objectsController;
	}

	public void setObjectsController(DAO objectsController) {
		this.objectsController = objectsController;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Timestamp getTheLastEntry() {
		return theLastEntry;
	}

	public void setTheLastEntry(Timestamp theLastEntry) {
		this.theLastEntry = theLastEntry;
	}

	public Settings getSettingsObject() {
		return settingsObject;
	}

	public void setSettingsObject(Settings settingsObject) {
		this.settingsObject = settingsObject;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SettingsConversion> getDataSettingsConversion() {
		return dataSettingsConversion;
	}

	public void setDataSettingsConversion(List<SettingsConversion> dataSettingsConversion) {
		this.dataSettingsConversion = dataSettingsConversion;
	}

	private void renumerateLines() {

		int i = 1;

		for (SettingsConversion e : dataSettingsConversion) {
			e.setLineNumber(i);
			i++;
		}
	}

	private void readAndFillBeanfromSetting(long pid) {

		settingsObject = objectsController.select(Settings.class, pid);

		// fill form values.
		writeFromSettingsObjectToSettingsForm();
	}

	// makes conversion of properties from db layer to controller
	private void writeFromSettingsObjectToSettingsForm() {
		id = settingsObject.getId();
		name = settingsObject.getName();
		user = settingsObject.getUser();
		theLastEntry = settingsObject.getTheLastEntry();

		List<SettingsConversion> listRead = settingsObject.getConversion();

		dataSettingsConversion.clear();

		// for new item it is null
		if (listRead != null) {
			// sort in ascending order
			Collections.sort(listRead, (b1, b2) -> (int) (b1.getLineNumber() - b2.getLineNumber()));

			for (SettingsConversion e : listRead) {
				dataSettingsConversion.add(e);
			}
		}

	}

}
