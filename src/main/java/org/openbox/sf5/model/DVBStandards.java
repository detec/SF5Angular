package org.openbox.sf5.model;

public enum DVBStandards {

	DVBS("DVB-S"), DVBS2("DVB-S2");

	private DVBStandards(String s) {
		value = s;
	}

	private final String value;

	@Override
	public String toString() {
		return value;
	}

}