package org.openbox.sf5.converters;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

// http://stackoverflow.com/questions/2519432/jaxb-unmarshal-timestamp

public class TimestampAdapter extends XmlAdapter<String, Timestamp> {

	private SimpleDateFormat dateFormat = getJsonDateFormatter();

	@Override
	public String marshal(Timestamp v) throws Exception {
		return dateFormat.format(v);
	}

	@Override
	public Timestamp unmarshal(String v) throws Exception {
		// String might be as nanoseconds
		// Timestamp value = new Timestamp(Long.parseLong(v));
		// value = (value == null) ? (Timestamp) dateFormat.parse(v) :
		// (Timestamp) dateFormat.parse(v);

		Date date = dateFormat.parse(v);
		Timestamp value = new Timestamp(date.getTime());
		return value;
	}

	public static SimpleDateFormat getJsonDateFormatter() {
		// SimpleDateFormat formatter = new
		// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		// formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		formatter.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));

		return formatter;
	}

}
