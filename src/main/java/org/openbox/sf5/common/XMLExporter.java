package org.openbox.sf5.common;

import java.util.ArrayList;
import java.util.List;

import org.openbox.sf5.model.Polarization;
import org.openbox.sf5.model.Sat;
import org.openbox.sf5.model.Sat.Satid;
import org.openbox.sf5.model.Sat.Satid.Tp;
import org.openbox.sf5.model.SettingsConversion;

// Should be replaced by http://stackoverflow.com/questions/13346101/generate-pojos-without-an-xsd
// https://wiki.eclipse.org/EclipseLink/Examples/MOXy/JAXB/Compiler

/**
 * Exports setting in a fixed XML format.
 *
 * @author Andrii Duplyk
 *
 */
public class XMLExporter {

	// xjc -d f:\My\Java\SF5Spring\src\main\java\org\openbox\sf5\model -encoding
	// UTF-8 f:\My\Java\SF5Spring\src\main\resources\xsd\sf5_format.xsd

	// https://github.com/FasterXML/jackson-module-jaxb-annotations/issues/51
	// Jackson JAXB problem is discussed there

	/**
	 * Main conversion method call.
	 *
	 * @param dataSettingsConversion
	 * @return constructed Sat element, ready for export to XML.
	 */
	public static Sat exportSettingsConversionPresentationToSF5Format(List<SettingsConversion> dataSettingsConversion) {

		// Generating sat/tp structure
		generateSatTp(dataSettingsConversion);

		Sat root = new Sat();

		// lambdas do not see variables
		List<Sat> stubSatList = new ArrayList<>();
		stubSatList.add(root);

		// we should iterate through lines and add elements from the deepest to
		// top.

		dataSettingsConversion.stream().forEach(e -> {

			Tp newTp = new Sat.Satid.Tp();
			newTp.setFreq((int) e.getTransponder().getFrequency());
			newTp.setIndex(String.valueOf(e.getTpindex()));
			newTp.setLnbFreq(String.valueOf(e.getTransponder().getCarrier()));
			newTp.setPolar(Integer.parseInt(Polarization.getXMLpresentation(e.getTransponder().getPolarization())));
			newTp.setSymbol((int) e.getTransponder().getSpeed());

			// check if satid exists
			if (root.getSatid().size() < e.getSatindex()) {
				// adding new empty entry
				root.getSatid().add(new Sat.Satid());
			}

			// add new Tp to Satid
			Satid currentSatid = root.getSatid().get((int) e.getSatindex() - 1);
			currentSatid.setIndex(String.valueOf(e.getSatindex()));
			currentSatid.getTp().add(newTp);
		});

		return root;

	}

	/**
	 * Fills sat and tp numbering.
	 *
	 * @param dataSettingsConversion
	 */
	public static void generateSatTp(List<SettingsConversion> dataSettingsConversion) {
		// Generating sat/tp structure
		long sat = 1;
		long currentCount = 0;
		for (SettingsConversion e : dataSettingsConversion) {
			currentCount++;
			e.setSatindex(sat);
			e.setTpindex(currentCount);
			if (currentCount == 4) {
				currentCount = 0;
				sat++;
			}
		}
	}

}
