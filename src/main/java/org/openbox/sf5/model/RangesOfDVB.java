package org.openbox.sf5.model;

/**
 * Ranges of DVB: Ku, C.
 * 
 * @author Andrii Duplyk
 *
 */
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