package com.linkstec.mock;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.linkstec.mock.excel.ExcelReader;
import com.linkstec.mock.excel.ExcelWriter;
import com.linkstec.mock.excel.vo.ExcelMetaData;
import com.linkstec.mock.processor.MockData4DB;



@RunWith(SpringRunner.class)
@SpringBootTest
public class MyTest {

	@Autowired
	private MockData4DB mock;

	@Autowired
	private ExcelReader reader;

	@Autowired
	private ExcelWriter writer;

	@Autowired
	private com.linkstec.mock.excel.v2.ExcelWriter writer2;

	@Autowired
	private com.linkstec.mock.excel.v2.ExcelReader reader2;

	@Value("${file.folder.template}")
	private String path;

	@Test
	public void generate() {
	    String input = path + "template.xlsx";
	    String output = path + "out.xlsx";
	    mock.MockData(input, output);
	}

	@Test
	public void read() {
		String input = path + "template.xlsx";
		try {
			ExcelMetaData metaData = reader.read(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void write() {
		String input = path + "template.xlsx";
		String output = path + "out.xlsx";
		try {
			ExcelMetaData metaData = reader.read(input);
			writer.write(metaData, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void read_v2() {
		String input = path + "template.v2.xlsx";
		try {
			 reader2.read(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Test
	public void write_v2() {
		String input = path + "template.v2.xlsx";
		String output = path + "out.v2.xlsx";
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
