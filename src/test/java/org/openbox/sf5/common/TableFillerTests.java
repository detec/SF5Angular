package org.openbox.sf5.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openbox.sf5.model.TheDVBRangeValues;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@ContextConfiguration(locations = { "file:src/test/resources/context/test-autowired-beans.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@Component
public class TableFillerTests extends AbstractJsonizerTest {

	// @Autowired
	// private TableFiller tf;

	@Before
	public void setUp() {
		super.setUpAbstract();

	}

	@Test
	@Transactional
	public void shouldFillTablesByTableFiller() {

		// executeTableFiller();

		// there should be 2 records in THEDVBRANGEVALUES
		List<TheDVBRangeValues> rangesList = DAO.findAll(TheDVBRangeValues.class);
		assertThat(rangesList.size()).isEqualTo(2);
	}

	// public void executeTableFiller() {
	// TableFiller tf = new TableFiller();
	// tf.setSessionFactory(sessionFactory);
	// tf.setObjectController(DAO);
	// tf.init();
	//
	// }

}
