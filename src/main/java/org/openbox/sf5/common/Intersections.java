package org.openbox.sf5.common;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.ProgressDialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jdbc.ReturningWork;
import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.model.Settings;
import org.openbox.sf5.model.SettingsConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Class that helps to find intersections of transponder frequencies in selected
 * settings lines.
 *
 * @author duplyk.a
 *
 */
@Service
public class Intersections {

	private static final String SELECT_CONSTANT = "select \n";

	@Autowired
	private DAO objectController;

	/**
	 * List of intersected lines.
	 */
	private List<Integer> arrayLines = new ArrayList<>();

	private SessionFactory getSessionFactory() {
		return objectController.getSessionFactory();
	}

	/**
	 * Main method that checks frequency intersections.
	 *
	 * @param dataSettingsConversion
	 *            set of lines
	 * @param object
	 *            {@link Settings} object
	 * @return number of intersections found
	 * @throws SQLException
	 */
	public int checkIntersection(List<SettingsConversion> dataSettingsConversion, Settings object) throws SQLException {

		Session session = objectController.openSession();

		ReturningWork<ResultSet> rowsReturningWork = getResultSet(object, dataSettingsConversion);
		session.doReturningWork(rowsReturningWork);

		session.close();

		// remove duplicates
		Set<Integer> hs = new HashSet<>();
		hs.addAll(arrayLines);
		arrayLines.clear();
		arrayLines.addAll(hs);

		return arrayLines.size();

	}

	private ReturningWork<ResultSet> getResultSet(Settings object, List<SettingsConversion> dataSettingsConversion) {

		ReturningWork<ResultSet> rowsReturningWork = new ReturningWork<ResultSet>() {

			@Override
			public ResultSet execute(Connection connection) throws SQLException {
				ResultSet resultSet = null;

				// syntax changed due to H2 and Postgre limitations.

				prepareTables(connection, object);

				try (PreparedStatement preparedStatement = connection.prepareStatement(getIntersectionQuery());) {
					resultSet = preparedStatement.executeQuery();

					// 11.08.2015, trying to remove locks
					// this removes lock from sys and temp tables.
					// 01.02.2016, commented as it gives error with EJB
					// IJ031020: You cannot commit with autocommit set
					if (!connection.getAutoCommit()) {
						connection.commit();
					}
					// 11.08.2015

					while (resultSet.next()) {
						int rowIndex = new BigDecimal(
								// 11.08.2015, there seems to be a bug in
								// defining rows.
								resultSet.getLong("lineNumber")).intValueExact();

						arrayLines.add(new Integer(rowIndex));

						SettingsConversion sc = dataSettingsConversion.get(rowIndex);

						long intersectionValue = resultSet.getLong("theLineOfIntersection");

						sc.setTheLineOfIntersection(intersectionValue + 1);
					}

					return resultSet;
				}

			}
		};

		return rowsReturningWork;

	}

	private void prepareTables(Connection connection, Settings object) throws SQLException {

		// http://stackoverflow.com/questions/1571928/retrieve-auto-detected-hibernate-dialect
		Dialect dialect = ((SessionFactoryImplementor) getSessionFactory()).getDialect();

		// drop tables
		try (PreparedStatement ps = connection.prepareStatement(getDropTempTables(dialect));) {
			ps.execute();
		}

		if (dialect instanceof MySQL5Dialect) {
			// With MySQL let's try to split temp tables creation.

			try (PreparedStatement preparedStatement = connection.prepareStatement(fillFirstTempTable(dialect));) {
				preparedStatement.setLong(1, object.getId());
				preparedStatement.execute();
			}

			try (PreparedStatement preparedStatement = connection.prepareStatement(fillSecondTempTable(dialect));) {
				preparedStatement.execute();
			}

			try (PreparedStatement preparedStatement = connection.prepareStatement(fillThirdTempTable(dialect));) {
				preparedStatement.execute();
			}

		} else {
			// fill temp tables
			try (PreparedStatement preparedStatement = connection.prepareStatement(fillTempTables(dialect));) {
				preparedStatement.setLong(1, object.getId());
				preparedStatement.execute();
			}
		}

	}

	private static String getDropTempTables(Dialect dialect) {
		String returnString = "";
		if (dialect instanceof H2Dialect) {
			returnString = "\n" + "DROP TABLE CONVERSIONTABLE IF EXISTS; \n"
					+ "DROP TABLE ManyFrequencies IF EXISTS; \n"

					+ "DROP TABLE IntersectionTable IF EXISTS; \n";
		}

		else if (dialect instanceof ProgressDialect) {
			returnString = "\n" + "DROP TABLE IF EXISTS CONVERSIONTABLE; \n"
					+ "DROP TABLE IF EXISTS ManyFrequencies;  \n"

					+ "DROP TABLE IF EXISTS IntersectionTable; \n";
		}

		else if (dialect instanceof MySQL5Dialect) {

			returnString = "\n "

					+ "DROP TABLE IF EXISTS CONVERSIONTABLE, ManyFrequencies, IntersectionTable;";
		}

		return returnString;
	}

	private static String fillTempTables(Dialect dialect) {
		// Adjusting to different database dialects.

		// http://stackoverflow.com/questions/1915074/understanding-the-in-javas-format-strings
		String tempTableCreate = dialect.getCreateTableString();
		String fromatString = "\n" + "%1$s CONVERSIONTABLE  AS ( \n" + SELECT_CONSTANT + "LineNumber \n"

				+ ", tp.frequency \n"

				+ ", 0 as TheLineOfIntersection \n"

				+ "	FROM SettingsConversion conv \n"

				+ "inner join Transponders tp \n"

				+ "on conv.Transponder = tp.id \n"

				+ " where parent_id = ? \n"

				+ " ); \n"

				+ "%1$s ManyFrequencies AS (  \n"

				+ SELECT_CONSTANT + "p1.LineNumber \n" + ", p1.frequency \n" + ", p1.TheLineOfIntersection \n"

				+ "from ConversionTable p1  \n"

				+ "union  \n"

				+ SELECT_CONSTANT + "p2.LineNumber  \n" + ", p2.frequency + 1 \n"
				+ ", p2.TheLineOfIntersection AS  TheLineOfIntersection \n" + "from ConversionTable p2 \n"

				+ "union \n"

				+ SELECT_CONSTANT + "p3.LineNumber \n" + ", p3.frequency - 1 \n" + ", p3.TheLineOfIntersection \n"
				+ "from ConversionTable p3 \n"

				+ " ); \n"

				+ "%1$s IntersectionTable AS (   \n"

				+ SELECT_CONSTANT + "t1.LineNumber \n" + ", t1.frequency \n"
				+ ", t2.LineNumber as TheLineOfIntersection \n"

				+ "from ManyFrequencies t1 \n" + "inner join ManyFrequencies t2 \n"
				+ "on t1.frequency = t2.frequency \n" + "and t1.LineNumber <> t2.LineNumber \n"

				+ " ); \n";

		return String.format(fromatString, tempTableCreate);

	}

	private static String fillFirstTempTable(Dialect dialect) {

		String tempTableCreate = dialect.getCreateTableString();

		String fromatString = "\n" + "%1$s CONVERSIONTABLE  AS ( \n" + SELECT_CONSTANT + "lineNumber \n"

				+ ", tp.frequency \n"

				+ ", 0 as theLineOfIntersection \n"

				+ "	FROM SettingsConversion conv \n"

				+ "inner join Transponders tp \n"

				+ "on conv.transponder = tp.id \n"

				+ " where parent_id = ? \n"

				+ " ); \n\n";

		return String.format(fromatString, tempTableCreate);

	}

	private static String fillSecondTempTable(Dialect dialect) {

		String tempTableCreate = dialect.getCreateTableString();

		String fromatString = "%1$s ManyFrequencies " + ""

				+ "\n"

				+ SELECT_CONSTANT + "p1.lineNumber \n" + ", p1.frequency \n" + ", p1.theLineOfIntersection \n"

				+ "from ConversionTable p1"

				+ " \n"

				+ "UNION \n"

				+ SELECT_CONSTANT + "p2.lineNumber \n" + ", p2.frequency + 1 \n"
				+ ", p2.theLineOfIntersection AS theLineOfIntersection \n" + "from ConversionTable p2 \n"

				+ "UNION \n"

				+ SELECT_CONSTANT + "p3.lineNumber \n" + ", p3.frequency - 1 \n" + ", p3.theLineOfIntersection \n"
				+ "from ConversionTable p3; \n\n";

		return String.format(fromatString, tempTableCreate);

	}

	private static String fillThirdTempTable(Dialect dialect) {

		String tempTableCreate = dialect.getCreateTableString();

		String fromatString = "%1$s IntersectionTable AS (   \n"

				+ SELECT_CONSTANT + "t1.lineNumber \n" + ", t1.frequency \n"
				+ ", t2.lineNumber as theLineOfIntersection \n"

				+ "from ManyFrequencies t1 \n" + "inner join ManyFrequencies t2 \n"
				+ "on t1.frequency = t2.frequency \n" + "and t1.lineNumber <> t2.lineNumber \n"

				+ " ); \n";

		return String.format(fromatString, tempTableCreate);

	}

	private static String getIntersectionQuery() {

		return "\n"

				+ "select distinct \n" + "conv.lineNumber \n"
				+ ", COALESCE(inter.theLineOfIntersection, 0) AS theLineOfIntersection \n"

				+ "from ConversionTable conv \n" + "inner join IntersectionTable inter \n"
				+ "on inter.lineNumber = conv.lineNumber \n"

				+ "order by \n" + "conv.lineNumber \n"

				+ "";

	}

}
