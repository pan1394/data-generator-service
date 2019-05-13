package com.linkstec.mock.excel.vo;

public class RelationFieldMap {

	private String no;

	private String leftPart;

	private String rightPart;

	private String value;

	public RelationFieldMap(String id, String leftPart, String rightRight) {
		this.no = id;
		this.leftPart = leftPart;
		this.rightPart = rightRight;
	}



	public String getLeftPart() {
		return leftPart;
	}

	public void setLeftPart(String leftPart) {
		this.leftPart = leftPart;
	}

	public String getRightRight() {
		return rightPart;
	}

	public void setRightRight(String rightRight) {
		this.rightPart = rightRight;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 是否包含某个字段
	 * @param field
	 * @return
	 */
	public boolean contains(String no, String field) {
		return this.no.equals(no) && (this.leftPart.equalsIgnoreCase(field) || this.rightPart.equalsIgnoreCase(field));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((leftPart == null) ? 0 : leftPart.hashCode());
		result = prime * result + ((rightPart == null) ? 0 : rightPart.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelationFieldMap other = (RelationFieldMap) obj;
		if (no == null) {
			if (other.no != null)
				return false;
		} else if (!no.equals(other.no))
			return false;
		if (leftPart == null) {
			if (other.leftPart != null)
				return false;
		} else if (!leftPart.equals(other.leftPart))
			return false;
		if (rightPart == null) {
			if (other.rightPart != null)
				return false;
		} else if (!rightPart.equals(other.rightPart))
			return false;
		return true;
	}


}
