package com.gopinathrm.sql.convertor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DDLConvertLoader {

	private static Map<String, String> dataTypes = new HashMap<String, String>();

	private static final Map<String, Properties> MAPPING_FILES = new HashMap<String, Properties>();

	static {

		Properties prop = new Properties();
		try {
			System.out.println();
			prop.load(new FileInputStream(System.getProperty("user.dir") + "\\dataTypeMapping.properties"));
			MAPPING_FILES.put("ORACLE_MYSQL", prop);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static final String ORACLE = "ORACLE";
	public static final String MYSQL = "MYSQL";
	public static final String END_OF_SCRIPT = ";";

	private String fromSQL;
	private String toSQL;

	public void convertDDL(File folder, String from, String to) {

		if (MAPPING_FILES.containsKey(from + "_" + to)) {
			for (final String name : MAPPING_FILES.get(from + "_" + to).stringPropertyNames())
				dataTypes.put(name, MAPPING_FILES.get(from + "_" + to).getProperty(name));
			this.setFromSQL(from);
			this.setToSQL(to);
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
				if (file.isFile() && file.getName().endsWith(".sql")) {
					List<SQLDDLPackage> listSQLDDLPackage = this.extractSQLFromFile(file.getAbsolutePath());
					this.dumpSQLFromPackage(listSQLDDLPackage, folder + "\\" + to + "\\" + file.getName());
				}
			}
		} else {
			System.out.println("Mapping.properties file not found!! [" + from + "-" + to + "]");
		}

	}

	public void dumpSQLFromPackage(List<SQLDDLPackage> listSQLDDLPackage, String outputFileName) {
		System.out.println("Final folder path :" + outputFileName);
		for (SQLDDLPackage sqlddlPackage : listSQLDDLPackage) {
			System.out.println(sqlddlPackage.getCreateStatement());
		}
	}

	public SQLDDLPackage convertToSpecfiledFormat(SQLDDLPackage sqlDDLPackage) {

		String replacedString = sqlDDLPackage.getCreateStatement().toString().toUpperCase();
		for (String dataType : dataTypes.keySet()) {
			if (!dataType.contains("NUMBER")) {
				replacedString = replacedString.replaceAll(dataType, dataTypes.get(dataType));
			}
		}

		replacedString = this.convertNumberFormats(replacedString);
		sqlDDLPackage.getCreateStatement().setLength(0);
		sqlDDLPackage.getCreateStatement().append(replacedString);
		return sqlDDLPackage;
	}

	public String convertNumberFormats(String inputString) {
		if (inputString.contains("NUMBER")) {
			boolean whiteSpace = true;
			while (whiteSpace) {
				if (inputString.contains("NUMBER ")) {
					inputString = inputString.replace("NUMBER ", "NUMBER");
				} else {
					whiteSpace = !whiteSpace;
				}
			}

			while (inputString.contains("NUMBER")) {
				int indexOfNum = inputString.indexOf("NUMBER");
				char symbol = inputString.charAt(indexOfNum + "NUMBER".length());
				if (Character.toString(symbol).matches("[,?]")) {
					inputString = inputString.replaceFirst("NUMBER", "NUMERIC");
				} else if (Character.toString(symbol).matches("[(?]")) {
					String dataType = inputString.substring(indexOfNum + "NUMBER".length(), indexOfNum + 14);// 14 -
																												// random
																												// number
					String lengthOfDataType = dataType.substring(dataType.indexOf("(") + 1, dataType.indexOf(")"));
					String lengthVal[] = lengthOfDataType.split(",");
					if (lengthVal.length > 0) {
						Integer intVal = Integer.parseInt(lengthVal[0]);
						String regex = inputString.substring(indexOfNum,
								indexOfNum + lengthOfDataType.length() + "NUMBER".length() + 2);
						regex="NUMBER";
						if (intVal <= 3) {
							inputString = inputString.replaceFirst(regex, "TINYINT");
						} else if (intVal > 3 && intVal <= 5) {
							inputString = inputString.replaceFirst(regex, "SMALLINT");
						} else if (intVal > 5 && intVal <= 7) {
							inputString = inputString.replaceFirst(regex, "MEDIUMINT");
						} else if (intVal > 7 && intVal <= 10) {
							inputString = inputString.replaceFirst(regex, "INTEGER");
						} else if (intVal > 10) {
							inputString = inputString.replaceFirst(regex, "BIGINT");
						}
					}
				}
			}

		}

		return inputString;
	}

	public List<SQLDDLPackage> extractSQLFromFile(String scriptInputFilePath) {
		List<SQLDDLPackage> listSQLDDLPackage = new LinkedList<SQLDDLPackage>();
		SQLDDLPackage sqlDDLPackage = new SQLDDLPackage();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(scriptInputFilePath));
			String line = reader.readLine();
			while (line != null) {

				sqlDDLPackage.getCreateStatement().append(line + "\n");
				if (line.contains(END_OF_SCRIPT)) {
					// call convertor
					System.out.println("convertinggg........");

					listSQLDDLPackage.add(this.convertToSpecfiledFormat(sqlDDLPackage));
					sqlDDLPackage = new SQLDDLPackage();
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listSQLDDLPackage;

	}

	public String getToSQL() {
		return toSQL;
	}

	public void setToSQL(String toSQL) {
		this.toSQL = toSQL;
	}

	public String getFromSQL() {
		return fromSQL;
	}

	public void setFromSQL(String fromSQL) {
		this.fromSQL = fromSQL;
	}

}
