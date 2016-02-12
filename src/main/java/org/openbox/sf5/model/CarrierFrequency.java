package org.openbox.sf5.model;

public enum CarrierFrequency {

	Lower("9750"), Top("10600"), CRange("5150"), TopPie("10750");

	private CarrierFrequency(String s) {
		value = s;
	}

	private final String value;

	@Override
	public String toString() {
		return value;
	}

}