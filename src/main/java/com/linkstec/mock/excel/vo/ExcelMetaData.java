package com.linkstec.mock.excel.vo;

import java.util.ArrayList;
import java.util.List;

public class ExcelMetaData {

	private FutureInfo future;
	private List<TableInfo> tables = new ArrayList<TableInfo>();
	private List<Case> caze= new ArrayList<Case>();
	private String type;
	private int recordCount;


	public FutureInfo getFuture() {
		return future;
	}

	public List<TableInfo> getTables() {
		return tables;
	}

	public List<Case> getCaze() {
		return caze;
	}

	public String getType() {
		return type;
	}

	public int getRecordCount() {
		return recordCount;
	}

	private ExcelMetaData(Builder b) {
		future = b.future;
		tables = b.tables;
		caze = b.caze;
		type = b.type;
		recordCount = b.recordCount;
	}

	public static class Builder{
		private FutureInfo future;
		private List<TableInfo> tables = new ArrayList<TableInfo>();
		private List<Case> caze= new ArrayList<Case>();
		private String type;
		private int recordCount;

		public Builder future(FutureInfo f) {
			future = f;
			return this;
		}

		public Builder cases(List<Case> cs) {
			caze = cs;
			return this;
		}

		public Builder tableInfo(List<TableInfo> tbls) {
			tables = tbls;
			return this;
		}

		public Builder type(String t) {
			type = t;
			return this;
		}

		public Builder recordCount(int count) {
			recordCount = count;
			return this;
		}

		public ExcelMetaData build() {
			return new ExcelMetaData(this);
		}
	}

}


