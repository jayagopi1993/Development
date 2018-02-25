package com.gopinathrm.sql.convertor;

public class SQLDDLPackage {

	private StringBuilder sqlStatement = new StringBuilder();

	public StringBuilder getSqlStatement() {
		return sqlStatement;
	}

	public void setSqlStatement(StringBuilder createStatement) {
		this.sqlStatement = createStatement;
	}

}
