package com.gopinathrm.sql.convertor;

import java.io.File;

public class MainLoader {

	public static void main(String[] args) {
		File folder = new File(AppConstants.INPUT_FOLDER);
		//DDLConvertLoader ddlConvertLoader = new DDLConvertLoader();
		//ddlConvertLoader.convertDDL(folder, AppConstants.ORACLE, AppConstants.MYSQL);
		DMLConvertLoader dmlConvertLoader = new DMLConvertLoaderImpl();
		dmlConvertLoader.convertDML(folder, AppConstants.ORACLE, AppConstants.MYSQL);
	}
}
