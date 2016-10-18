package org.openbox.sf5.model;

public enum RangesOfDVB {

	KU("Ku"), C("C");

	private final String value;

	private RangesOfDVB(String s) {
		value = s;
	}

	@Override
	public String toString() {
		return value;
	}

}