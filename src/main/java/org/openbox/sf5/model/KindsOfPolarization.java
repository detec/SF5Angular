package org.openbox.sf5.model;

public enum KindsOfPolarization {

	LINEAR("Linear"), PIE("Pie");

	private final String value;

	private KindsOfPolarization(String s) {
		value = s;
	}

	@Override
	public String toString() {
		return value;
	}

}