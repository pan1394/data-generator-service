package com.linkstec.mock.excel.vo.v2;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.mock.excel.vo.FutureInfo;
import com.linkstec.mock.excel.vo.TableInfo;

public class ExcelMetaData {

	private FutureInfo future;
	private List<TableInfo> tables = new ArrayList<TableInfo>();
	private List<Case> caze= new ArrayList<Case>();


	public FutureInfo getFuture() {
		return future;
	}

	public List<TableInfo> getTables() {
		return tables;
	}

	public List<Case> getCaze() {
		return caze;
	}

	private ExcelMetaData(Builder b) {
		future = b.future;
		tables = b.tables;
		caze = b.caze;
	}

	public static class Builder{
		private FutureInfo future;
		private List<TableInfo> tables = new ArrayList<TableInfo>();
		private List<Case> caze= new ArrayList<Case>();

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

		public ExcelMetaData build() {
			return new ExcelMetaData(this);
		}
	}

}


