package com.linkstec.mock.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.linkstec.mock.mapper.MasterMapper;

@Component
public class MasterMapperExecutor {

	@Autowired
	private MasterMapper masterMapper;
	
	private Map<String, Supplier<List<String>>> mapSuppliers = new HashMap<>();
	
	public MasterMapperExecutor() {
		mapSuppliers.put("constant", new Supplier<List<String>>() {
			@Override
			public List<String> get() {
				return masterMapper.getConstantId();
			}
		});
		mapSuppliers.put("null", new Supplier<List<String>>() {
			@Override
			public List<String> get() {
				return masterMapper.getNULL();
			}
		});
	}
	
	public List<String> execute(String field){
		return this.mapSuppliers.get(field).get();
	}
	
	public List<String> executeCustomSql(String customSql){
		return this.masterMapper.getListFields(customSql);
	}
}
