package com.ibm.pdf.dao.connection;

import java.sql.Connection;
import java.util.Set;

import com.ibm.db2.jcc.DB2SimpleDataSource;
import com.ibm.nosql.json.api.BasicDBList;
import com.ibm.nosql.json.api.BasicDBObject;
import com.ibm.nosql.json.util.JSON;

public class SQLDBConnection {

	private static SQLDBConnection sqldbConnection = null;

	private static String databaseHost = null;
	private static int port = 0;
	private static String databaseName = null;
	private static String user = null;
	private static String password = null;
	private static String url = null;

	private SQLDBConnection() {

	}

	public static SQLDBConnection getSQLDBConnectionInstance() {
		try {
			if (sqldbConnection == null) {
				if (processVCAP()) {
					sqldbConnection = new SQLDBConnection();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqldbConnection;
	}

	public static Connection getConnection() {

		Connection connection = null;
		try {
			DB2SimpleDataSource dataSource = new DB2SimpleDataSource();
			dataSource.setServerName(databaseHost);
			dataSource.setPortNumber(port);
			dataSource.setDatabaseName(databaseName);
			dataSource.setUser(user);
			dataSource.setPassword(password);
			dataSource.setDriverType(4);
			connection = dataSource.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	private static boolean processVCAP() {
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		System.out.println("VCAP_SERVICES content: " + VCAP_SERVICES);

		if (VCAP_SERVICES != null) {
			// parse the VCAP JSON structure
			BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
			String thekey = null;
			Set<String> keys = obj.keySet();
			System.out.println("Searching through VCAP keys");
			// Look for the VCAP key that holds the SQLDB information
			for (String eachkey : keys) {
				System.out.println("Key is: " + eachkey);
				// Just in case the service name gets changed to lower case in
				// the future, use toUpperCase
				if (eachkey.toUpperCase().contains("SQLDB")) {
					thekey = eachkey;
				}
			}
			if (thekey == null) {
				System.out
						.println("Cannot find any SQLDB service in the VCAP; exiting");
				return false;
			}
			BasicDBList list = (BasicDBList) obj.get(thekey);
			obj = (BasicDBObject) list.get("0");
			System.out.println("Service found: " + obj.get("name"));
			// parse all the credentials from the vcap env variable
			obj = (BasicDBObject) obj.get("credentials");
			databaseHost = (String) obj.get("host");
			databaseName = (String) obj.get("db");
			port = (int) obj.get("port");
			user = (String) obj.get("username");
			password = (String) obj.get("password");
			url = (String) obj.get("jdbcurl");
		} else {
			System.out.println("VCAP_SERVICES is null");
			return false;
		}
		return true;
	}

}
