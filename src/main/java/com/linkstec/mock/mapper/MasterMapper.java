package com.linkstec.mock.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MasterMapper {

	@Select("${customSql}")
	public List<String> getListFields(@Param("customSql") String customSql);

	@Select("Select 10011")
	public List<String> getConstantId();
	
	@Select("Select NULL")
	public List<String> getNULL();
	
}
