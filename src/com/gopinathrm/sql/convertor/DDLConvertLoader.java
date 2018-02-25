package com.gopinathrm.sql.convertor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DDLConvertLoader {

	private String fromSQL;
	private String toSQL;

	private static Map<String, String> dataTypes = new HashMap<String, String>();

	public void convertDDL(File folder, String from, String to) {

		if (AppConstants.MAPPING_FILES_DDL.containsKey(from + "_" + to)) {
			for (final String name : AppConstants.MAPPING_FILES_DDL.get(from + "_" + to).stringPropertyNames())
				dataTypes.put(name, AppConstants.MAPPING_FILES_DDL.get(from + "_" + to).getProperty(name));
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
				if (file.isFile() && file.getName().endsWith(".sql")) {
					List<SQLDDLPackage> listSQLDDLPackage = this.extractSQLFromFile(file.getAbsolutePath());
					this.dumpSQLFromPackage(listSQLDDLPackage, folder + "\\" + to, file.getName());
				}
			}
		} else {
			System.out.println("DDL Mapping.properties file not found!! [" + from + "-" + to + "]");
		}

	}

	public void dumpSQLFromPackage(List<SQLDDLPackage> listSQLDDLPackage, String outputFolderName,
			String outputFileName) {
		Path path = Paths.get(outputFolderName);
		try {
			Files.createDirectories(path);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		File file = new File(outputFolderName + "\\" + outputFileName);
		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(file);
			for (SQLDDLPackage sqlddlPackage : listSQLDDLPackage) {
				fileWriter.write(sqlddlPackage.getSqlStatement().toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done!!");

	}

	public SQLDDLPackage convertToSpecfiledFormat(SQLDDLPackage sqlDDLPackage) {

		String replacedString = sqlDDLPackage.getSqlStatement().toString().toUpperCase();

		if (replacedString.contains(AppConstants.CREATE_STATEMENT)) {

			for (String dataType : dataTypes.keySet()) {
				if (!dataType.contains("NUMBER")) {
					replacedString = replacedString.replace(dataType, dataTypes.get(dataType));
				}
			}

			replacedString = this.convertNumberFormats(replacedString);
			sqlDDLPackage.getSqlStatement().setLength(0);
			sqlDDLPackage.getSqlStatement().append(replacedString);

		} else if (replacedString.contains(AppConstants.ALTER_STATEMENT)) {
			if (replacedString.contains(AppConstants.DROP_CONSTRAINT)) {
				replacedString = replacedString.substring(0, replacedString.indexOf(AppConstants.DROP_CONSTRAINT));
				sqlDDLPackage.getSqlStatement().setLength(0);
				sqlDDLPackage.getSqlStatement().append(replacedString + "DROP PRIMARY KEY;");
			}
		} else if (replacedString.contains(AppConstants.DROP_INDEX)) {
			System.out.println("Can n't convert Drop index statment -- Please try manualy :\n");
			System.out.println("can Help on syntax :[ Oracle syntax: " + replacedString + "]");
			System.out.println("[equalent MySQL syntax : ALTER TABLE <table name> " + replacedString + "]");
		}
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
					String dataType = inputString.substring(indexOfNum + "NUMBER".length(), indexOfNum + 14);
					String lengthOfDataType = dataType.substring(dataType.indexOf("(") + 1, dataType.indexOf(")"));
					String lengthVal[] = lengthOfDataType.split(",");
					if (lengthVal.length > 0) {
						Integer intVal = Integer.parseInt(lengthVal[0]);
						String regex = inputString.substring(indexOfNum,
								indexOfNum + lengthOfDataType.length() + "NUMBER".length() + 2);
						if (intVal <= 3) {
							inputString = inputString.replace(regex, "TINYINT");
						} else if (intVal > 3 && intVal <= 5) {
							inputString = inputString.replace(regex, "SMALLINT");
						} else if (intVal > 5 && intVal <= 7) {
							inputString = inputString.replace(regex, "MEDIUMINT");
						} else if (intVal > 7 && intVal <= 10) {
							inputString = inputString.replace(regex, "INTEGER");
						} else if (intVal > 10) {
							inputString = inputString.replace(regex, "BIGINT");
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

				sqlDDLPackage.getSqlStatement().append(line + "\n");
				if (line.contains(AppConstants.END_OF_SCRIPT)) {
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

	public String getFromSQL() {
		return fromSQL;
	}

	public void setFromSQL(String fromSQL) {
		this.fromSQL = fromSQL;
	}

	public String getToSQL() {
		return toSQL;
	}

	public void setToSQL(String toSQL) {
		this.toSQL = toSQL;
	}

}
