package org.openbox.sf5.common;

import java.util.ArrayList;
import java.util.List;

import org.openbox.sf5.model.Polarization;
import org.openbox.sf5.model.Sat;
import org.openbox.sf5.model.Sat.Satid;
import org.openbox.sf5.model.Sat.Satid.Tp;
import org.openbox.sf5.model.SettingsConversion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

// Should be replaced by http://stackoverflow.com/questions/13346101/generate-pojos-without-an-xsd
// https://wiki.eclipse.org/EclipseLink/Examples/MOXy/JAXB/Compiler

public class XMLExporter {

	// xjc -d f:\My\Java\SF5Spring\src\main\java\org\openbox\sf5\model -encoding
	// UTF-8 f:\My\Java\SF5Spring\src\main\resources\xsd\sf5_format.xsd

	// https://github.com/FasterXML/jackson-module-jaxb-annotations/issues/51
	// Jackson JAXB problem is discussed there

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
			newTp.setPolar(
					Integer.valueOf(Polarization.getXMLpresentation(e.getTransponder().getPolarization())).intValue());
			newTp.setSymbol((int) e.getTransponder().getSpeed());

			// int indexOfE = dataSettingsConversion.indexOf(e);
			// int currentLineNumber = indexOfE + 1;

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

	// utility method to create text node
	private static Node getTpElements(Document doc, String name, String value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}

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
