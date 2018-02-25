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

public class DMLConvertLoaderImpl implements DMLConvertLoader {

	private static Map<String, String> dataTypes = new HashMap<String, String>();

	@Override
	public void convertDML(File folder, String from, String to) {

		if (AppConstants.MAPPING_FILES_DML.containsKey(from + "_" + to)) {
			for (final String name : AppConstants.MAPPING_FILES_DML.get(from + "_" + to).stringPropertyNames())
				dataTypes.put(name, AppConstants.MAPPING_FILES_DML.get(from + "_" + to).getProperty(name));
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
				if (file.isFile() && file.getName().endsWith(".sql")) {
					List<SQLDDLPackage> listSQLDDLPackage = this.extractSQLFromFile(file.getAbsolutePath());
					this.dumpSQLFromPackage(listSQLDDLPackage, folder + "\\" + to, file.getName());
				}
			}
		} else {
			System.out.println("DML Mapping.properties file not found!! [" + from + "-" + to + "]");
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

		if (replacedString.contains(AppConstants.INSERT_STATEMENT)) {

			for (String dataType : dataTypes.keySet()) {
				replacedString = replacedString.replaceAll(dataType, dataTypes.get(dataType));
			}

			sqlDDLPackage.getSqlStatement().setLength(0);
			sqlDDLPackage.getSqlStatement().append(replacedString);

		}
		return sqlDDLPackage;
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

}
