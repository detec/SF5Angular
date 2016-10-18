package org.openbox.sf5.model;

/**
 * Types of FEC
 *
 * @author Andrii Duplyk
 *
 */
public enum TypesOfFEC {

	_12("1-2"), _23("2-3"), _34("3-4"), _56("5-6"), _78("7-8"), _89("8-9"), _91("9-10"), _35("3-5");

	private final String value;

	private TypesOfFEC(String s) {
		value = s;
	}

	@Override
	public String toString() {
		return value;
	}

}