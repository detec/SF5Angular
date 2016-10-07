package org.openbox.sf5.common;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.internal.TypeLocatorImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.EnumType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.model.CarrierFrequency;
import org.openbox.sf5.model.DVBStandards;
import org.openbox.sf5.model.Polarization;
import org.openbox.sf5.model.RangesOfDVB;
import org.openbox.sf5.model.Satellites;
import org.openbox.sf5.model.TheDVBRangeValues;
import org.openbox.sf5.model.Transponders;
import org.openbox.sf5.model.TypesOfFEC;
import org.openbox.sf5.model.ValueOfTheCarrierFrequency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Class to parse transponder files.
 *
 * @author duplyk.a
 *
 */
@Service
public class IniReader {

	private Satellites sat;

	@Autowired
	private DAO objectController;

	private static final String REGEX = "(\\d{1,3})=(\\d{5}),(H|V|L|R),(\\d{4,5}),(\\d{2}),(DVB-S|S2),(QPSK|8PSK)(\\sACM)?";
	private static final String FREQUENCY_CONSTANT = "Frequency";
	private Pattern pattern;
	private Matcher matcher;

	private boolean result = false;

	private String filePath;

	public IniReader() {
		// default constructor
	}

	/**
	 * Accepts {@link MultipartFile} from controller
	 *
	 * @param file
	 *            {@link MultipartFile}
	 * @throws IOException
	 */
	public void readMultiPartFile(MultipartFile file) throws IOException {

		// create a temp file
		File temp = File.createTempFile("transponders", ".xml");
		String absolutePath = temp.getAbsolutePath();

		byte[] bytes = file.getBytes();
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(absolutePath)));
		stream.write(bytes);
		stream.close();

		// calling reader class
		setFilePath(absolutePath);
		readData(); // doing import
	}

	/**
	 * Method used for unit test.
	 *
	 * @throws IOException
	 */
	public void readData() throws IOException {
		// Open the file

		FileReader fileReader = new FileReader(filePath);
		BufferedReader br = new BufferedReader(fileReader);

		String strLine;

		// (\d{1,3})=(\d{5}),(H|V|L|R),(\d{4,5}),(\d{2,3}),(DVB-S|S2),(QPSK|8PSK)(\sACM)?

		// Read File Line By Line
		while ((strLine = br.readLine()) != null) {

			if ("[SATTYPE]".equals(strLine)) {
				readSatData(br);
			}

			if ("[DVB]".equals(strLine)) {
				readTransponderData(br);
			}

		}

		fileReader.close();
		br.close();

		result = true;

	}

	private void readSatData(BufferedReader br) throws IOException {

		br.readLine(); // 1=0130
		String satline = br.readLine();

		String satName = satline.substring(2); // 2 characters

		String hql = "select id from Satellites where name = :name";

		Session session = objectController.openSession();

		Query query = session.createQuery(hql);
		query.setParameter("name", satName);
		@SuppressWarnings("unchecked")
		ArrayList<Long> rs = (ArrayList<Long>) query.list();

		if (rs.isEmpty()) {
			// no satellite found
			sat = new Satellites(satName);

			// saving satellite
			objectController.saveOrUpdate(sat);
		} else {
			// get sat
			sat = objectController.select(Satellites.class, rs.get(0));
		}

		session.close();
	}

	private void readTransponderData(BufferedReader br) throws IOException {

		// replace with Java core

		String transCountString = br.readLine().substring(2);

		int transCount = Integer.parseInt(transCountString);

		pattern = Pattern.compile(REGEX);

		for (int i = 1; i <= transCount; i++) {
			String transDataString = br.readLine();

			// Initialize

			matcher = pattern.matcher(transDataString);

			while (matcher.find()) {
				processTransponedrMatch();
			}
		}
	}

	private void processTransponedrMatch() {

		Transponders selectedTrans;

		// name will be transponder number in

		// let's check, that it isn't Multistream
		String multistream = matcher.group(8);
		if (multistream != null) {
			return;
		}

		// frequency
		String frequencyString = matcher.group(2);

		Long frequency = Long.parseLong(frequencyString);

		// polarization
		Polarization aPolarization = Polarization.valueOf(matcher.group(3));

		// speed
		Long speed = Long.parseLong(matcher.group(4));

		// FEC
		TypesOfFEC fec = TypesOfFEC.valueOf("_" + matcher.group(5));

		// DVB standard
		DVBStandards dvbStandard = resolveDVBStandard();

		Session session = objectController.openSession();

		// define range
		RangesOfDVB rangeEnum = resolveTheDVBRangeValue(session, frequency);

		CarrierFrequency carrierEnum = resolveCarrierFrequency(session, frequency, aPolarization);

		Transponders newTrans = new Transponders(frequency, aPolarization, fec, carrierEnum, speed, dvbStandard,
				rangeEnum, sat);

		// let's check if such frequency already exists on the given
		// satellite
		List<Object> transIdList = getListOfTranspondersWithSameFrequency(session, frequency);

		if (transIdList.isEmpty()) {
			objectController.saveOrUpdate(newTrans);
		}

		else {

			long transId = (long) transIdList.get(0);
			selectedTrans = objectController.select(Transponders.class, transId);

			// check if this trans changed to newly read trans
			if (!selectedTrans.equals(newTrans)) {
				// we should update all properties of the selected
				// trans.
				updateTransponderData(selectedTrans, carrierEnum, fec, frequency, aPolarization, rangeEnum, speed,
						dvbStandard);
			}

		}

	}

	private List<Object> getListOfTranspondersWithSameFrequency(Session session, Long frequency) {

		String sqltext = "Select id FROM Transponders where frequency = :Frequency and satellite = :satelliteId";

		List<Object> transIdList;
		transIdList = session.createSQLQuery(sqltext).addScalar("id", StandardBasicTypes.LONG)

				.setParameter(FREQUENCY_CONSTANT, frequency)

				.setParameter("satelliteId", sat.getId()).list();

		session.close();

		return transIdList;
	}

	private RangesOfDVB resolveTheDVBRangeValue(Session session, Long frequency) {
		RangesOfDVB rangeEnum = null;

		Properties params = new Properties();
		params.put("enumClass", RangesOfDVB.class.getName());
		params.put("type", "12"); /*
									 * type 12 instructs to use the String
									 * representation of enum value
									 */
		Type myEnumType = new TypeLocatorImpl(new TypeResolver()).custom(EnumType.class, params);

		String sqltext = "SELECT rangeOfDVB FROM TheDVBRangeValues where :Frequency between lowerThreshold and upperThreshold";

		List<TheDVBRangeValues> range = session.createSQLQuery(sqltext).addScalar("rangeOfDVB", myEnumType)
				.setParameter(FREQUENCY_CONSTANT, frequency)
				.setResultTransformer(Transformers.aliasToBean(TheDVBRangeValues.class)).list();

		if (!range.isEmpty()) {
			rangeEnum = range.get(0).getRangeOfDVB();
		}
		return rangeEnum;

	}

	private DVBStandards resolveDVBStandard() {
		DVBStandards dvbStandard = null;
		String standard = matcher.group(6);
		if ("DVB-S".equals(standard)) {
			dvbStandard = DVBStandards.DVBS;
		}

		if ("S2".equals(standard)) {
			dvbStandard = DVBStandards.DVBS2;
		}

		return dvbStandard;
	}

	private CarrierFrequency resolveCarrierFrequency(Session session, Long frequency, Polarization aPolarization) {
		CarrierFrequency carrierEnum = null;

		// get carrier frequency
		Properties params = new Properties();
		params.put("enumClass", CarrierFrequency.class.getName());
		params.put("type", "12"); /*
									 * type 12 instructs to use the String
									 * representation of enum value
									 */
		Type myEnumType = new TypeLocatorImpl(new TypeResolver()).custom(EnumType.class, params);

		String sqltext = "SELECT typeOfCarrierFrequency FROM ValueOfTheCarrierFrequency "
				+ "where (:Frequency between lowerThreshold and upperThreshold) "
				+ "and (polarization = :KindOfPolarization)";

		@SuppressWarnings("unchecked")
		List<ValueOfTheCarrierFrequency> carrierList = session.createSQLQuery(sqltext)

				.addScalar("typeOfCarrierFrequency", myEnumType)

				.setParameter(FREQUENCY_CONSTANT, frequency)

				.setParameter("KindOfPolarization", Polarization.getPolarizationKind(aPolarization).ordinal())

				.setResultTransformer(Transformers.aliasToBean(ValueOfTheCarrierFrequency.class)).list();

		if (!carrierList.isEmpty()) {
			carrierEnum = carrierList.get(0).getTypeOfCarrierFrequency();
		} else {
			return carrierEnum;
		}
		return carrierEnum;

	}

	private void updateTransponderData(Transponders selectedTrans, CarrierFrequency carrierEnum, TypesOfFEC fec,
			Long frequency, Polarization aPolarization, RangesOfDVB rangeEnum, Long speed, DVBStandards dvbStandard) {

		selectedTrans.setCarrier(carrierEnum);
		selectedTrans.setFEC(fec);
		selectedTrans.setFrequency(frequency);
		selectedTrans.setPolarization(aPolarization);
		selectedTrans.setRangeOfDVB(rangeEnum);
		selectedTrans.setSatellite(sat);
		selectedTrans.setSpeed(speed);
		selectedTrans.setVersionOfTheDVB(dvbStandard);
		objectController.update(selectedTrans);

	}

	public DAO getObjectController() {
		return objectController;
	}

	public void setObjectController(DAO objectController) {
		this.objectController = objectController;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filepath) {
		this.filePath = filepath;
	}

}
