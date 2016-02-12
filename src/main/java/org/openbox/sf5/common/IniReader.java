package org.openbox.sf5.common;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

@Service
public class IniReader implements Serializable {

	public IniReader() {

	}

	public void readMultiPartFile(MultipartFile file) throws IOException {

		// create a temp file
		File temp = File.createTempFile("transponders", ".xml");
		String absolutePath = temp.getAbsolutePath();

		byte[] bytes = file.getBytes();
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(absolutePath)));
		stream.write(bytes);
		stream.close();

		// calling reader class
		setFilepath(absolutePath);
		readData(); // doing import
	}

	public void readData() throws IOException {
		// Open the file
		FileInputStream fstream = new FileInputStream(filepath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		// (\d{1,3})=(\d{5}),(H|V|L|R),(\d{4,5}),(\d{2,3}),(DVB-S|S2),(QPSK|8PSK)(\sACM)?

		// Read File Line By Line
		while ((strLine = br.readLine()) != null) {

			if (strLine.equals("[SATTYPE]")) {
				readSatData(br);
			}

			if (strLine.equals("[DVB]")) {
				readTransponderData(br);
			}

		}

		fstream.close();
		br.close();

		result = true;

	}

	private void readSatData(BufferedReader br) throws IOException {

		br.readLine(); // 1=0130
		String satline = br.readLine();

		String satName = satline.substring(2); // 2 characters

		String hql = "select id from Satellites where name = :name";

		Session session = sessionFactory.openSession();

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

	@SuppressWarnings("unchecked")
	private void readTransponderData(BufferedReader br) throws IOException {

		Transponders selectedTrans = null;
		// replace with Java core

		String transCountString = br.readLine().substring(2);

		int transCount = Integer.parseInt(transCountString);

		pattern = Pattern.compile(REGEX);

		for (int i = 1; i <= transCount; i++) {
			String transDataString = br.readLine();

			// Initialize

			matcher = pattern.matcher(transDataString);
			RangesOfDVB rangeEnum = null;
			CarrierFrequency carrierEnum = null;

			// 62=11919,V,27500,23,S2,8PSK ACM/VCM
			// int count = 0;
			while (matcher.find()) {
				// count++;

				// name will be transponder number in
				// String Name = matcher.group(1);

				// let's check, that it isn't Multistream
				String Multistream = matcher.group(8);
				if (Multistream != null) {
					continue;
				}

				// frequency
				String FrequencyString = matcher.group(2);
				// System.out.println(FrequencyString);
				Long Frequency = Long.parseLong(FrequencyString);

				// polarization
				Polarization aPolarization = Polarization.valueOf(matcher.group(3));

				// speed
				Long Speed = Long.parseLong(matcher.group(4));

				// FEC
				TypesOfFEC FEC = TypesOfFEC.valueOf("_" + matcher.group(5));

				// DVB standard
				DVBStandards DVBStandard = null;
				String Standard = matcher.group(6);
				if (Standard.equals("DVB-S")) {
					DVBStandard = DVBStandards.DVBS;
				}

				if (Standard.equals("S2")) {
					DVBStandard = DVBStandards.DVBS2;
				}

				// define range

				Properties params = new Properties();
				params.put("enumClass", RangesOfDVB.class.getName());
				params.put("type",
						"12"); /*
								 * type 12 instructs to use the String
								 * representation of enum value
								 */
				Type myEnumType = new TypeLocatorImpl(new TypeResolver()).custom(EnumType.class, params);

				String sqltext = "SELECT rangeOfDVB FROM TheDVBRangeValues where :Frequency between lowerThreshold and upperThreshold";

				Session session = sessionFactory.openSession();

				List<TheDVBRangeValues> range = session.createSQLQuery(sqltext).addScalar("rangeOfDVB", myEnumType)
						.setParameter("Frequency", Frequency)
						.setResultTransformer(Transformers.aliasToBean(TheDVBRangeValues.class)).list();

				if (!range.isEmpty()) {
					rangeEnum = range.get(0).getRangeOfDVB();
				} else {
					continue;
				}

				// get carrier frequency
				params = new Properties();
				params.put("enumClass", CarrierFrequency.class.getName());
				params.put("type",
						"12"); /*
								 * type 12 instructs to use the String
								 * representation of enum value
								 */
				myEnumType = new TypeLocatorImpl(new TypeResolver()).custom(EnumType.class, params);

				sqltext = "SELECT typeOfCarrierFrequency FROM ValueOfTheCarrierFrequency "
						+ "where (:Frequency between lowerThreshold and upperThreshold) "
						+ "and (polarization = :KindOfPolarization)";

				List<ValueOfTheCarrierFrequency> carrierList = session.createSQLQuery(sqltext)

						.addScalar("typeOfCarrierFrequency", myEnumType)

						.setParameter("Frequency", Frequency)

						.setParameter("KindOfPolarization", Polarization.getPolarizationKind(aPolarization).ordinal())

						.setResultTransformer(Transformers.aliasToBean(ValueOfTheCarrierFrequency.class)).list();

				if (!carrierList.isEmpty()) {
					carrierEnum = carrierList.get(0).getTypeOfCarrierFrequency();
				} else {
					continue;
				}

				// let's check if such frequency already exists on the given
				// satellite
				sqltext = "Select id FROM Transponders where frequency = :Frequency and satellite = :satelliteId";

				List<Object> transIdList = new ArrayList<>();
				transIdList = session.createSQLQuery(sqltext).addScalar("id", StandardBasicTypes.LONG)

						.setParameter("Frequency", Frequency)

						.setParameter("satelliteId", sat.getId()).list();

				Transponders newTrans = new Transponders(Frequency, aPolarization, FEC, carrierEnum, Speed, DVBStandard,
						rangeEnum, sat);

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
						selectedTrans.setCarrier(carrierEnum);
						selectedTrans.setFEC(FEC);
						selectedTrans.setFrequency(Frequency);
						selectedTrans.setPolarization(aPolarization);
						selectedTrans.setRangeOfDVB(rangeEnum);
						selectedTrans.setSatellite(sat);
						selectedTrans.setSpeed(Speed);
						selectedTrans.setVersionOfTheDVB(DVBStandard);
						objectController.update(selectedTrans);
					}

				}

				session.close();
			}
		}
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public DAO getObjectController() {
		return objectController;
	}

	public void setObjectController(DAO objectController) {
		this.objectController = objectController;
	}

	private static final long serialVersionUID = -1699774508872380035L;

	private Satellites sat;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private DAO objectController;

	final String REGEX = "(\\d{1,3})=(\\d{5}),(H|V|L|R),(\\d{4,5}),(\\d{2}),(DVB-S|S2),(QPSK|8PSK)(\\sACM)?";
	private static Pattern pattern;
	private static Matcher matcher;

	private boolean result = false;

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	private String filepath;

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

}
