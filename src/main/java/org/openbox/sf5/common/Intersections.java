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
import org.openbox.sf5.model.Settings;
import org.openbox.sf5.model.SettingsConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Intersections {

	@Autowired
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	List<Integer> arrayLines = new ArrayList<Integer>();

	public int checkIntersection(List<SettingsConversion> dataSettingsConversion, Settings Object) throws SQLException {

		ReturningWork<ResultSet> rowsReturningWork = new ReturningWork<ResultSet>() {

			@Override
			public ResultSet execute(Connection connection) throws SQLException {
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;

				// syntax changed due to H2 and Postgre limitations.

				// http://stackoverflow.com/questions/1571928/retrieve-auto-detected-hibernate-dialect
				Dialect dialect = ((SessionFactoryImplementor) sessionFactory).getDialect();

				// String tempTableDrop = dialect.getDropTemporaryTableString();

				// drop tables
				try {
					preparedStatement = connection.prepareStatement(getDropTempTables(dialect));
					preparedStatement.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				try {
					// fill temp tables
					// preparedStatement =
					// connection.prepareStatement(fillTempTables(dialect));
					// preparedStatement.setLong(1, Object.getId());
					// preparedStatement.execute();

					if (dialect instanceof MySQL5Dialect) {
						// With MySQL let's try to split temp tables creation.
						preparedStatement = connection.prepareStatement(fillFirstTempTable(dialect));
						preparedStatement.setLong(1, Object.getId());
						preparedStatement.execute();

						preparedStatement = connection.prepareStatement(fillSecondTempTable(dialect));
						preparedStatement.execute();

						preparedStatement = connection.prepareStatement(fillThirdTempTable(dialect));
						preparedStatement.execute();

					} else {
						// fill temp tables
						preparedStatement = connection.prepareStatement(fillTempTables(dialect));
						preparedStatement.setLong(1, Object.getId());
						preparedStatement.execute();
					}

					preparedStatement = connection.prepareStatement(getIntersectionQuery());
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

						long IntersectionValue = resultSet.getLong("theLineOfIntersection");

						sc.setTheLineOfIntersection(IntersectionValue + 1);
					}

					return resultSet;
				} catch (SQLException e) {
					throw e;
				}

			}
		};

		Session session = sessionFactory.openSession();

		ResultSet rs = session.doReturningWork(rowsReturningWork);

		session.close();

		// remove duplicates
		Set<Integer> hs = new HashSet<>();
		hs.addAll(arrayLines);
		arrayLines.clear();
		arrayLines.addAll(hs);
		// return rows;
		return arrayLines.size();

	}

	public static String getDropTempTables(Dialect dialect) {
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
			// returnString = "\n " + "DROP TEMPORARY TABLE IF EXISTS
			// CONVERSIONTABLE; \n"
			// + "DROP TEMPORARY TABLE IF EXISTS ManyFrequencies; \n"
			// + "DROP TEMPORARY TABLE IF EXISTS IntersectionTable; \n ";
			returnString = "\n "
					// + "DROP TEMPORARY TABLE IF EXISTS CONVERSIONTABLE,
					// ManyFrequencies, IntersectionTable;";
					+ "DROP TABLE IF EXISTS CONVERSIONTABLE, ManyFrequencies, IntersectionTable;";
		}

		return returnString;
	}

	public static String fillTempTables(Dialect dialect) {
		// Adjusting to different ddatabase dialects.

		// return "\n" + "CREATE MEMORY TEMPORARY TABLE CONVERSIONTABLE AS ( \n"
		// + "SELECT \n" + "LineNumber \n"
		// return

		// http://stackoverflow.com/questions/1915074/understanding-the-in-javas-format-strings
		String tempTableCreate = dialect.getCreateTableString();
		String fromatString = "\n" + "%1$s CONVERSIONTABLE  AS ( \n" + "SELECT \n" + "LineNumber \n"

				+ ", tp.frequency \n"

				+ ", 0 as TheLineOfIntersection \n"

				// + " into #ConversionTable \n"

				+ "	FROM SettingsConversion conv \n"

				+ "inner join Transponders tp \n"

				+ "on conv.Transponder = tp.id \n"

				+ " where parent_id = ? \n"

				+ " ); \n"

				// + "CREATE MEMORY TEMPORARY TABLE ManyFrequencies AS ( \n"
				+ "%1$s ManyFrequencies AS (  \n"

				+ "select \n" + "p1.LineNumber \n" + ", p1.frequency \n" + ", p1.TheLineOfIntersection \n"
				// + "into #ManyFrequencies \n"

				+ "from ConversionTable p1  \n"

				+ "union  \n"

				+ "select  \n" + "p2.LineNumber  \n" + ", p2.frequency + 1 \n"
				+ ", p2.TheLineOfIntersection AS  TheLineOfIntersection \n" + "from ConversionTable p2 \n"

				+ "union \n"

				+ "select \n" + "p3.LineNumber \n" + ", p3.frequency - 1 \n" + ", p3.TheLineOfIntersection \n"
				+ "from ConversionTable p3 \n"

				+ " ); \n"

				// + "CREATE MEMORY TEMPORARY TABLE IntersectionTable AS ( \n"
				+ "%1$s IntersectionTable AS (   \n"

				+ "select \n" + "t1.LineNumber \n" + ", t1.frequency \n" + ", t2.LineNumber as TheLineOfIntersection \n"
				// + "into #IntersectionTable \n"
				+ "from ManyFrequencies t1 \n" + "inner join ManyFrequencies t2 \n"
				+ "on t1.frequency = t2.frequency \n" + "and t1.LineNumber <> t2.LineNumber \n"

				+ " ); \n";

		return String.format(fromatString, tempTableCreate);

	}

	public static String fillFirstTempTable(Dialect dialect) {

		// String tempTableCreate = "";
		//
		// if (dialect instanceof MySQL5Dialect) {
		// tempTableCreate = "CREATE TEMPORARY TABLE IF NOT EXISTS";
		// } else {
		// tempTableCreate = dialect.getCreateTableString();
		// }

		String tempTableCreate = dialect.getCreateTableString();

		String fromatString = "\n" + "%1$s CONVERSIONTABLE  AS ( \n" + "SELECT \n" + "lineNumber \n"

				+ ", tp.frequency \n"

				+ ", 0 as theLineOfIntersection \n"

				// + " into #ConversionTable \n"

				+ "	FROM SettingsConversion conv \n"

				+ "inner join Transponders tp \n"

				+ "on conv.transponder = tp.id \n"

				+ " where parent_id = ? \n"

				+ " ); \n\n";

		String resultQuery = String.format(fromatString, tempTableCreate);

		return resultQuery;

	}

	public static String fillSecondTempTable(Dialect dialect) {

		// String tempTableCreate = "";
		//
		// if (dialect instanceof MySQL5Dialect) {
		// tempTableCreate = "CREATE TEMPORARY TABLE IF NOT EXISTS";
		// } else {
		// tempTableCreate = dialect.getCreateTableString();
		// }

		String tempTableCreate = dialect.getCreateTableString();

		String fromatString = "%1$s ManyFrequencies " + ""

				+ "\n"

				+ "select \n" + "p1.lineNumber \n" + ", p1.frequency \n" + ", p1.theLineOfIntersection \n"

				+ "from ConversionTable p1"

				+ " \n"

				+ "UNION \n"

				+ "select \n" + "p2.lineNumber \n" + ", p2.frequency + 1 \n"
				+ ", p2.theLineOfIntersection AS theLineOfIntersection \n" + "from ConversionTable p2 \n"

				+ "UNION \n"

				+ "select \n" + "p3.lineNumber \n" + ", p3.frequency - 1 \n" + ", p3.theLineOfIntersection \n"
				+ "from ConversionTable p3; \n\n";

		String resultQuery = String.format(fromatString, tempTableCreate);

		return resultQuery;

	}

	public static String fillThirdTempTable(Dialect dialect) {

		// String tempTableCreate = "";
		//
		// if (dialect instanceof MySQL5Dialect) {
		// tempTableCreate = "CREATE TEMPORARY TABLE IF NOT EXISTS";
		// } else {
		// tempTableCreate = dialect.getCreateTableString();
		// }

		String tempTableCreate = dialect.getCreateTableString();

		String fromatString = "%1$s IntersectionTable AS (   \n"

				+ "select \n" + "t1.lineNumber \n" + ", t1.frequency \n" + ", t2.lineNumber as theLineOfIntersection \n"
				// + "into #IntersectionTable \n"
				+ "from ManyFrequencies t1 \n" + "inner join ManyFrequencies t2 \n"
				+ "on t1.frequency = t2.frequency \n" + "and t1.lineNumber <> t2.lineNumber \n"

				+ " ); \n";

		String resultQuery = String.format(fromatString, tempTableCreate);

		return resultQuery;

	}

	public static String getIntersectionQuery() {

		return "\n"

				+ "select distinct \n" + "conv.lineNumber \n"
				+ ", COALESCE(inter.theLineOfIntersection, 0) AS theLineOfIntersection \n"

				+ "from ConversionTable conv \n" + "inner join IntersectionTable inter \n"
				+ "on inter.lineNumber = conv.lineNumber \n"

				+ "order by \n" + "conv.lineNumber \n"

				+ "";

	}

}
