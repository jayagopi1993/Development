package com.gopinathrm.sql.convertor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AppConstants {

	public static final String ORACLE = "ORACLE";
	public static final String MYSQL = "MYSQL";
	public static final String END_OF_SCRIPT = ";";
	public static final String INPUT_FOLDER = "D:\\Workspace\\JavaWS\\OracleToMysl\\DDLFiles";
	
	public static final String CREATE_STATEMENT = "CREATE TABLE";
	public static final String ALTER_STATEMENT = "ALTER TABLE";
	public static final String DROP_STATEMENT = "DROP TABLE";
	public static final String INSERT_STATEMENT = "INSERT INTO";
	public static final String DELETE_STATEMENT = "DELETE TABLE";
	public static final String DROP_CONSTRAINT = "DROP CONSTRAINT";
	public static final String DROP_INDEX = "DROP INDEX";

	public static final Map<String, Properties> MAPPING_FILES_DDL = new HashMap<String, Properties>();

	static {

		Properties prop = new Properties();
		try {
			System.out.println();
			prop.load(new FileInputStream(System.getProperty("user.dir") + "\\oracle-mysql-DDLMapping.properties"));
			MAPPING_FILES_DDL.put("ORACLE_MYSQL", prop);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static final Map<String, Properties> MAPPING_FILES_DML = new HashMap<String, Properties>();

	static {

		Properties prop = new Properties();
		try {
			System.out.println();
			prop.load(new FileInputStream(System.getProperty("user.dir") + "\\oracle-mysql-DMLMapping.properties"));
			MAPPING_FILES_DML.put("ORACLE_MYSQL", prop);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
