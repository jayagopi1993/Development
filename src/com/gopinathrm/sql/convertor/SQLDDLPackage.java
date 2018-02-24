package com.gopinathrm.sql.convertor;

public class SQLDDLPackage {

	private StringBuilder createStatement = new StringBuilder();
	private StringBuilder dropStatement = new StringBuilder();
	private StringBuilder alterStatement = new StringBuilder();

	public StringBuilder getCreateStatement() {
		return createStatement;
	}

	public void setCreateStatement(StringBuilder createStatement) {
		this.createStatement = createStatement;
	}

	public StringBuilder getDropStatement() {
		return dropStatement;
	}

	public void setDropStatement(StringBuilder dropStatement) {
		this.dropStatement = dropStatement;
	}

	public StringBuilder getAlterStatement() {
		return alterStatement;
	}

	public void setAlterStatement(StringBuilder alterStatement) {
		this.alterStatement = alterStatement;
	}

}
