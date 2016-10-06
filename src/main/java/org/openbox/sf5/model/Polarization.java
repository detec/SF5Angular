package org.openbox.sf5.model;

public enum Polarization {

	H("H"), L("L"), R("R"), V("V");

	private Polarization(String s) {
		value = s;
	}

	private final String value;

	@Override
	public String toString() {
		return value;
	}

	public static KindsOfPolarization getPolarizationKind(Polarization Pol) {

		KindsOfPolarization aKindsOfPolarization = KindsOfPolarization.Linear;

		if (Pol.equals(H) || Pol.equals(V)) {
			aKindsOfPolarization = KindsOfPolarization.Linear;
		}
		if (Pol.equals(R) || Pol.equals(L)) {
			aKindsOfPolarization = KindsOfPolarization.Pie;
		}

		return aKindsOfPolarization;

	}

	public static String getXMLpresentation(Polarization Pol) {
		String result = "";
		if (Pol.equals(H) || Pol.equals(L)) {
			result = "1";
		}
		if (Pol.equals(V) || Pol.equals(R)) {
			result = "0";
		}
		return result;
	}

}