package com.linkstec.mock.excel.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RelationFieldMapList {

	private List<RelationFieldMap> container = new ArrayList<>();

	public boolean add(RelationFieldMap m) {
		return container.add(m);
	}

	public List<RelationFieldMap> getRelationFieldMapsBy(String no, String field){
		return container.stream().filter( o -> o.contains(no, field)).collect(Collectors.toList());
	}

	/**
	 * 判断是否存在
	 * @param m
	 * @return
	 */
	public boolean isExists(RelationFieldMap m) {
		return container.stream().anyMatch( o-> o.equals(m));
	}


}
