package org.openbox.sf5.model;

public enum DVBStandards {

	DVBS("DVB-S"), DVBS2("DVB-S2");

	private final String value;

	private DVBStandards(String s) {
		value = s;
	}

	@Override
	public String toString() {
		return value;
	}

}