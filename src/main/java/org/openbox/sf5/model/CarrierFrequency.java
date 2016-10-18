package org.openbox.sf5.model;

/**
 * Carrier frequency enum
 *
 * @author Andrii Duplyk
 *
 */
public enum CarrierFrequency {

	LOWER("9750"), TOP("10600"), C_RANGE("5150"), TOP_PIE("10750");

	private CarrierFrequency(String s) {
		value = s;
	}

	private final String value;

	@Override
	public String toString() {
		return value;
	}

}