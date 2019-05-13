package com.linkstec.mock.vo;

public class ColumnVo {

	private String columnId;
	private String name;
	private String type;
	private String value;
	private String maxLength;
	private String isNullable;
	private String isIdentity;
	private String precision;
	private String scale;
	private String isPk;


	public String getIsPk() {
		return isPk;
	}
	public void setIsPk(String isPk) {
		this.isPk = isPk;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getColumnId() {
		return columnId;
	}
	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		if(type.equals("nemeric")|| type.equals("decimal")){
			return String.format("%s|%s(%s,%s)|%s|%s", this.name, this.type, this.precision, this.scale, this.isIdentity, this.isPk);
		}
		return String.format("%s|%s(%s)|%s|%s", this.name, this.type, this.maxLength, this.isIdentity, this.isPk);
	}

	public void setType(String type) {
		this.type = type;
	}
	public String getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}
	public String getIsNullable() {
		return isNullable;
	}
	public void setIsNullable(String isNullable) {
		this.isNullable = isNullable;
	}
	public String getIsIdentity() {
		return isIdentity;
	}
	public void setIsIdentity(String isIdentity) {
		this.isIdentity = isIdentity;
	}
	public String getPrecision() {
		return precision;
	}
	public void setPrecision(String precision) {
		this.precision = precision;
	}
	public String getScale() {
		return scale;
	}
	public void setScale(String scale) {
		this.scale = scale;
	}


}
