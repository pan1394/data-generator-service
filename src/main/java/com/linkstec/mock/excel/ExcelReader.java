package com.linkstec.mock.excel;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.linkstec.mock.excel.vo.Case;
import com.linkstec.mock.excel.vo.Condition;
import com.linkstec.mock.excel.vo.ExcelMetaData;
import com.linkstec.mock.excel.vo.ExcelMetaData.Builder;
import com.linkstec.mock.excel.vo.TableInfo;
import com.linkstec.mock.utils.ExcelUtils;
import com.linkstec.mock.vo.Result;

@Component
public class ExcelReader {

	public ExcelMetaData read(String input) throws Exception {
		try (InputStream inputStream = new FileInputStream(input);
			 XSSFWorkbook workbook = new XSSFWorkbook(inputStream);) {

			XSSFSheet sheetAt = workbook.getSheetAt(0);

			int rowIndex = 9;
			Builder metaBuilder = new ExcelMetaData.Builder();

			List<TableInfo> tableInfos = new ArrayList<>();
			Row row = sheetAt.getRow(rowIndex);
			while (row != null) {
				TableInfo table = new TableInfo();
				table.setId(ExcelUtils.getStringValue(row, 0));
				table.setName(ExcelUtils.getStringValue(row, 1));
				table.setAlias(ExcelUtils.getStringValue(row, 2));
				tableInfos.add(table);
				row = sheetAt.getRow(++rowIndex);
			}
			metaBuilder.tableInfo(tableInfos);

			rowIndex = rowIndex + 3;
			row = sheetAt.getRow(rowIndex);
			int count = Integer.parseInt(ExcelUtils.getStringValue(row, 2));
			metaBuilder.recordCount(count);

			rowIndex++;
			row = sheetAt.getRow(rowIndex);
			String type = ExcelUtils.getStringValue(row, 2);
			metaBuilder.type(type);

			rowIndex = rowIndex + 2;
			List<Case> cases = new ArrayList<Case>();
			row = sheetAt.getRow(rowIndex);
			while (row != null) {
				Case testCase = new Case();
				List<Condition> conditisons = new ArrayList<Condition>();
				Result result = ExcelUtils.isMergedRegion(sheetAt, rowIndex, 0);
				testCase.setCaseNumber(ExcelUtils.getMergedRegionCellValue(sheetAt, result));
				int end = result.endRow;
				boolean flag = false;
				while (rowIndex < end) {
					Condition condition = new Condition();
					condition.setLeft(ExcelUtils.getStringValue(row, 1));
					condition.setSymbol(ExcelUtils.getStringValue(row, 2));
					condition.setRight(ExcelUtils.getStringValue(row, 3));
					conditisons.add(condition);
					row = sheetAt.getRow(++rowIndex);
					flag = true;
				}

				testCase.setConditions(conditisons);
				cases.add(testCase);
				if(!flag) {
					row = sheetAt.getRow(++rowIndex);
				}
			}
			return metaBuilder.cases(cases).build();
		}
	}
}
