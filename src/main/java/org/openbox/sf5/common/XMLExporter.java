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
		List<Sat> stubSatList = new ArrayList<Sat>();
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

	// public static String
	// exportSettingToXML(List<SettingsConversionPresentation>
	// dataSettingsConversion) {
	//
	// // Generating sat/tp structure
	// generateSatTpPresentation(dataSettingsConversion);
	//
	// String absolutePath = "";
	// try {
	//
	// // create a temp file
	// File temp = File.createTempFile("transponders", ".xml");
	// absolutePath = temp.getAbsolutePath();
	//
	// } catch (IOException e) {
	//
	// return "";
	//
	// }
	//
	// DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
	// DocumentBuilder icBuilder;
	// try {
	// icBuilder = icFactory.newDocumentBuilder();
	// Document doc = icBuilder.newDocument();
	// Element mainRootElement = doc.createElement("sat");
	// doc.appendChild(mainRootElement);
	//
	// long oldsatindex = 0;
	// Element satId = null;
	//
	// for (SettingsConversionPresentation e : dataSettingsConversion) {
	// long currentSatIndex = e.getSatindex();
	// if (currentSatIndex != oldsatindex) {
	// oldsatindex = currentSatIndex;
	// satId = doc.createElement("satid");
	// satId.setAttribute("index", String.valueOf(currentSatIndex));
	// mainRootElement.appendChild(satId);
	// }
	//
	// satId.appendChild(getTransponderNode(doc, e));
	//
	// }
	//
	// Transformer transformer =
	// TransformerFactory.newInstance().newTransformer();
	// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	//
	// DOMSource source = new DOMSource(doc);
	// StreamResult fileresult = new StreamResult(new File(absolutePath));
	// transformer.transform(source, fileresult);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return absolutePath;
	//
	// }

	// utility method to create text node
	private static Node getTpElements(Document doc, String name, String value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}

	// private static Node getTransponderNode(Document doc,
	// SettingsConversionPresentation e) {
	// Element tpId = null;
	//
	// long currentTpIndex = e.getTpindex();
	// tpId = doc.createElement("tp");
	// tpId.setAttribute("index", String.valueOf(currentTpIndex));
	// tpId.appendChild(getTpElements(doc, "LnbFreq",
	// String.valueOf(e.getTransponder().getCarrier())));
	// tpId.appendChild(getTpElements(doc, "Freq",
	// String.valueOf(e.getTransponder().getFrequency())));
	// tpId.appendChild(getTpElements(doc, "Symbol",
	// String.valueOf(e.getTransponder().getSpeed())));
	// tpId.appendChild(getTpElements(doc, "Polar",
	// org.openbox.sf5.model.Polarization.getXMLpresentation(e.getTransponder().getPolarization())));
	// return tpId;
	// }

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

	// public static void
	// generateSatTpPresentation(List<SettingsConversionPresentation>
	// dataSettingsConversion) {
	// // Generating sat/tp structure
	// long sat = 1;
	// long currentCount = 0;
	// for (SettingsConversion e : dataSettingsConversion) {
	// currentCount++;
	// e.setSatindex(sat);
	// e.setTpindex(currentCount);
	// if (currentCount == 4) {
	// currentCount = 0;
	// sat++;
	// }
	// }
	// }

}
