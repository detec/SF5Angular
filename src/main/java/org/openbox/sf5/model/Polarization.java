package org.openbox.sf5.model;

/**
 * General kinds of polarization: horizontal, vertical.
 *
 * @author Andrii Duplyk
 *
 */
public enum Polarization {

	H("H"), L("L"), R("R"), V("V");

	private final String value;

	private Polarization(String s) {
		value = s;
	}

	@Override
	public String toString() {
		return value;
	}

	/**
	 * Define Kind of Polarization by its general kind.
	 *
	 * @param Pol
	 *            polarization
	 * @return
	 */
	public static KindsOfPolarization getPolarizationKind(Polarization pol) {

		KindsOfPolarization aKindsOfPolarization = KindsOfPolarization.LINEAR;

		if (pol.equals(H) || pol.equals(V)) {
			aKindsOfPolarization = KindsOfPolarization.LINEAR;
		}
		if (pol.equals(R) || pol.equals(L)) {
			aKindsOfPolarization = KindsOfPolarization.PIE;
		}

		return aKindsOfPolarization;

	}

	/**
	 * Define XML representation of polarization.
	 *
	 * @param Pol
	 *            polarization
	 * @return
	 */
	public static String getXMLpresentation(Polarization pol) {
		String result = "";
		if (pol.equals(H) || pol.equals(L)) {
			result = "1";
		}
		if (pol.equals(V) || pol.equals(R)) {
			result = "0";
		}
		return result;
	}

}