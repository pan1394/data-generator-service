package com.linkstec.mock.excel.v2;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.linkstec.mock.excel.vo.TableInfo;
import com.linkstec.mock.excel.vo.v2.Case;
import com.linkstec.mock.excel.vo.v2.Condition;
import com.linkstec.mock.excel.vo.v2.ExcelMetaData;
import com.linkstec.mock.excel.vo.v2.ExcelMetaData.Builder;
import com.linkstec.mock.utils.ExcelUtils;
import com.linkstec.mock.vo.Result;

/**
 *
 * @author panyilin
 *
 */
@Component("reader2")
public class ExcelReader {

	public ExcelMetaData read(String input) throws Exception {
		try (InputStream inputStream = new FileInputStream(input);
			 XSSFWorkbook workbook = new XSSFWorkbook(inputStream);) {

			XSSFSheet sheetAt = workbook.getSheetAt(0);
			Builder metaBuilder = new ExcelMetaData.Builder();

			//start row
			int rowIndex = 0;
			String cellValue = getCellValue(sheetAt, rowIndex, 0);
			while(!"テーブルID".equals(cellValue)) {
				rowIndex ++;
				cellValue = getCellValue(sheetAt, rowIndex, 0);
			}
			//情报row
			rowIndex ++;
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

			//关联row
			cellValue = getCellValue(sheetAt, rowIndex, 0);
			while(!"No.".equals(cellValue)) {
				rowIndex ++;
				cellValue = getCellValue(sheetAt, rowIndex, 0);
			}
			Result resultNo = ExcelUtils.isMergedRegion(sheetAt, rowIndex, 0);
			rowIndex = resultNo.endRow;
			//rowIndex = 15;


			List<Case> cases = new ArrayList<Case>();
			row = sheetAt.getRow(rowIndex);
			while (row != null) {
				Case testCase = new Case();
				List<Condition> conditisons = new ArrayList<Condition>();

				Result result0 = ExcelUtils.isMergedRegion(sheetAt, rowIndex, 0);
				testCase.setCaseNumber(ExcelUtils.getMergedRegionCellValue(sheetAt, result0));
				Result result1 = ExcelUtils.isMergedRegion(sheetAt, rowIndex, 1);
				testCase.setRecordsCount(Integer.parseInt(ExcelUtils.getMergedRegionCellValue(sheetAt, result1)));
				Result result2 = ExcelUtils.isMergedRegion(sheetAt, rowIndex, 2);
				testCase.setType(ExcelUtils.getMergedRegionCellValue(sheetAt, result2));
				int end = result0.endRow;
				boolean flag = false;
				while (rowIndex < end) {
					Condition condition = new Condition();
					condition.setLeft(ExcelUtils.getStringValue(row, 3));
					condition.setSymbol(ExcelUtils.getStringValue(row, 4));
					condition.setRight(ExcelUtils.getStringValue(row, 5));
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

	private String getCellValue(XSSFSheet sheet, int rowIndex, int columId) {
		Row row = sheet.getRow(rowIndex);
		if(row == null) {
			row = sheet.createRow(rowIndex);
			return "";
		}
		return ExcelUtils.getCellValue(row.getCell(columId));
	}
}
