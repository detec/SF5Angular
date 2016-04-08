package org.openbox.sf5.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamResult;

import org.hibernate.criterion.Criterion;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openbox.sf5.model.Sat;
import org.openbox.sf5.model.Settings;
import org.openbox.sf5.model.SettingsConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@ContextConfiguration(locations = { "file:src/test/resources/context/test-autowired-beans.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class VerifyXMLExporterTests extends AbstractJsonizerTest {

	@Autowired
	public Jaxb2Marshaller springMarshaller;

	@Before
	public void setUp() {
		super.setUpAbstract();
	}

	@Test
	public void shouldVerifyXMLExport() throws IOException, JAXBException, URISyntaxException {
		// we previously saved this setting
		Criterion criterion = criterionService.getCriterionByClassFieldAndStringValue(Settings.class, "name",
				"Intersections test");

		List<Settings> settList = DAO.ObjectsCriterionList(Settings.class, criterion);

		assertEquals(1, settList.size());

		Settings setting = settList.get(0);

		List<SettingsConversion> conversionLines = setting.getConversion();
		assertThat(conversionLines.size()).isGreaterThan(0);

		Sat sat = XMLExporter.exportSettingsConversionPresentationToSF5Format(conversionLines);

		// http://www.concretepage.com/spring/spring-jaxb-integration-annotation-pretty-print-example-with-jaxb2marshaller
		// try (FileOutputStream fos = new
		// FileOutputStream("sf5Jnitoutput.xml");) {
		// springMarshaller.marshal(sat, new StreamResult(fos));
		// }

		StringWriter sw = new StringWriter();

		URL responseFile = ClassLoader.getSystemResource("xml/sf5Jnitoutput.xml");
		assertThat(responseFile).isNotNull();

		URI uri = responseFile.toURI();
		assertThat(uri).isNotNull();

		String content = new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("UTF-8"));
		content = content.replace("\r\n\r\n", "\r\n"); // it adds
		// superfluous
		// \r\n

		// marshalling sat
		springMarshaller.marshal(sat, new StreamResult(sw));

		assertEquals(content, sw.toString());

	}

}
