package org.openbox.sf5.json.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class CustomXMLMapper extends XmlMapper {

	public CustomXMLMapper() {
		// to enable standard indentation ("pretty-printing"):
		configure(SerializationFeature.INDENT_OUTPUT, true);
		// to allow serialization of "empty" POJOs (no properties to serialize)
		// (without this setting, an exception is thrown in those cases)
		configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		// to write java.util.Date, Calendar as number (timestamp):
		configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		// DeserializationFeature for changing how JSON is read as POJOs:

		// to prevent exception when encountering unknown property:
		configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// to allow coercion of JSON empty String ("") to null Object value:
		configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

		// http://stackoverflow.com/questions/14319573/jersey-can-not-deserialize-instance-of-arraylist-out-of-string
		// To prevent java.lang.IllegalArgumentException: Can not deserialize
		// instance of java.util.ArrayList out of START_OBJECT token
		// at [Source: N/A; line: -1, column: -1] (through reference chain:
		// java.util.ArrayList[0]->org.openbox.sf5.model.Settings["user"]->org.openbox.sf5.model.Users["authorities"])
		configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	}

	private static final long serialVersionUID = 7048533734012373317L;

}
