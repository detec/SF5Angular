package org.openbox.sf5.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Used DVB standards.
 *
 * @author Andrii Duplyk
 *
 */
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

    /**
     * Helps to convert DVB standards from transponders file.
     *
     * @return - map for conversion
     */
    public static Map<String, DVBStandards> getConversionMap() {
        Map<String, DVBStandards> dvbMap = new HashMap<>();
        dvbMap.put("DVB-S", DVBStandards.DVBS);
        dvbMap.put("S2", DVBStandards.DVBS2);
        return dvbMap;
    }
}