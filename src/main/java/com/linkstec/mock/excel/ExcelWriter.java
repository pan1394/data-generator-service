package com.linkstec.mock.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.linkstec.mock.excel.vo.Case;
import com.linkstec.mock.excel.vo.Condition;
import com.linkstec.mock.excel.vo.ExcelMetaData;
import com.linkstec.mock.excel.vo.TableInfo;
import com.linkstec.mock.mapper.TableInfoMapper;
import com.linkstec.mock.utils.ConditionRightPartHolder;
import com.linkstec.mock.utils.ExcelUtils;
import com.linkstec.mock.utils.MasterMapperExecutor;
import com.linkstec.mock.utils.MockUtils;
import com.linkstec.mock.vo.ColumnVo;

@Component
public class ExcelWriter {

	@Autowired
	private TableInfoMapper tableInfoMapper;

	@Autowired
	private MasterMapperExecutor executor;

	public void write(ExcelMetaData data, String output) throws Exception {

		int rowIndex = 1;
		List<TableInfo> tables = data.getTables();
		String dataType = data.getType();
		int count = data.getRecordCount();
		List<Case> cases = data.getCaze();

		try (
			XSSFWorkbook wb = new XSSFWorkbook();
			FileOutputStream out = new FileOutputStream(output);){

			XSSFCellStyle borderStyle = ExcelUtils.getStyleBorder(wb);
			XSSFCellStyle colorStyle = ExcelUtils.getStyleBorderAndColor(wb);
			XSSFSheet sheet = wb.createSheet("データ");// 创建Excel工作表对象

			HashMap<String, List<String>> pkList = new HashMap<>();
			HashMap<String, String> fkList = new HashMap<>();

			for (TableInfo entry : tables) {
				String tableId = entry.getId();
				String tableAlias = entry.getAlias();

				sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex + 3, 0, 0));
				Row row = sheet.createRow(rowIndex);

				row.createCell(0).setCellValue("No");
				row.getCell(0).setCellStyle(colorStyle);
				row.createCell(1).setCellValue("DB状態");
				row.getCell(1).setCellStyle(colorStyle);
				sheet.createRow(rowIndex + 1).createCell(1).setCellValue(tableId);
				sheet.getRow(rowIndex + 1).getCell(1).setCellStyle(colorStyle);

				List<ColumnVo> columns = tableInfoMapper.getColumns(tableId);
				List<String> typeList = new ArrayList<>();
				int col = 1;
				XSSFRow fieldName = sheet.createRow(rowIndex + 2);
				XSSFRow extraNmae = sheet.createRow(rowIndex + 3);
				for (ColumnVo rs : columns) {
					fieldName.createCell(col).setCellValue(rs.getName());
					fieldName.getCell(col).setCellStyle(colorStyle);
					extraNmae.createCell(col).setCellValue(rs.getValue());
					extraNmae.getCell(col).setCellStyle(colorStyle);
					typeList.add(rs.getType());
					col++;
				}
				for (int a = 2; a <= typeList.size(); a++) {
					sheet.getRow(rowIndex).createCell(a).setCellStyle(colorStyle);
					sheet.getRow(rowIndex + 1).createCell(a).setCellStyle(colorStyle);
				}

				rowIndex = rowIndex + 4;
				for (Case testCase : cases) {
					HashMap<String, String> conditionList = new HashMap<>();
					List<Condition> conditions = testCase.getConditions();

					for (Condition c : conditions) {
						if (c.getLeft().indexOf(".") != -1 && (c.getLeft().indexOf(tableId + ".") == 0
								|| c.getLeft().indexOf(tableAlias + ".") == 0)) {
							if (c.getRight().indexOf(".") != -1 && !StringUtils.isNumeric(c.getRight())) {
								pkList.put(
										testCase.getCaseNumber() + "|" + tableId
												+ c.getLeft().substring(c.getLeft().indexOf(".")),
										new ArrayList<String>());
							}
							if (c.getRight().indexOf(".") == -1) {
								conditionList.put(tableId + c.getLeft().substring(c.getLeft().indexOf(".")),
										c.getRight());
							}
						}
						if (c.getRight().indexOf(".") != -1 && (c.getRight().indexOf(tableId + ".") == 0
								|| c.getRight().indexOf(tableAlias + ".") == 0)) {
							String alias = c.getLeft().substring(0, c.getLeft().indexOf("."));
							String tableName = getTableName(tables, alias);
							int colNum = 0;
							for (; colNum < typeList.size(); colNum++) {
								if (typeList.get(colNum)
										.indexOf(c.getRight().substring(c.getRight().indexOf(".") + 1)) == 0) {
									break;
								}
							}
							fkList.put(tableId + c.getRight().substring(c.getRight().indexOf(".")),
									testCase.getCaseNumber() + "|" + tableName
											+ c.getLeft().substring(c.getLeft().indexOf(".")) + "|" + rowIndex + "|"
											+ (1 + colNum));
						}
					}
					for (int k = 0; k < count; k++) {
						int cols = 1;
						XSSFRow colRow = sheet.createRow(rowIndex);
						for (String type : typeList) {
							String[] arr = type.split("\\|");
							String field = arr[0];
							String typeStr = arr[1];
							String value;
							if (arr.length > 2 && arr[2].equals("1")) {
								value = Integer.toString(k + 1);
							} else {
								value = MockUtils.getValue(typeStr, dataType);
							}
							List<String> pks = pkList.get(testCase.getCaseNumber() + "|" + tableId + "." + field);
							if (pks != null) {
								pks.add(value);
							}
							if (fkList.get(testCase.getCaseNumber() + "|" + tableId + "." + field) != null) {
								continue;
							}
							if (conditionList.get(tableId + "." + field) != null) {
								value = new ConditionRightPartHolder(executor, conditionList.get(tableId + "." + field))
										.get();
							}
							colRow.createCell(cols).setCellValue(value);
							colRow.getCell(cols).setCellStyle(borderStyle);
							cols++;
						}
						rowIndex++;
					}
					sheet.addMergedRegion(new CellRangeAddress(rowIndex - count, rowIndex - 1, 0, 0));
					sheet.getRow(rowIndex - count).createCell(0).setCellValue(testCase.getCaseNumber());
					sheet.getRow(rowIndex - count).getCell(0).setCellStyle(borderStyle);
				}
				rowIndex++;
			}
			if (fkList.size() > 0) {
				for (Map.Entry<String, String> entry : fkList.entrySet()) {
					String[] arg = entry.getValue().split("\\|");
					List<String> values = pkList.get(arg[0] + "|" + arg[1]);
					for (int i1 = 0; i1 < count; i1++) {
						sheet.getRow(Integer.parseInt(arg[2]) + i1).createCell(Integer.parseInt(arg[3]))
								.setCellValue(getRandomList(values));
						sheet.getRow(Integer.parseInt(arg[2]) + i1).getCell(Integer.parseInt(arg[3]))
								.setCellStyle(borderStyle);
					}
				}
			}


			out.flush();
			wb.write(out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}



	private String getRandomList(List<String> list) {
		Random random = new Random();
		int n = random.nextInt(list.size());
		return list.get(n);
	}

	private static String getTableName(List<TableInfo> tables, String alias) {
		return tables.stream().filter(o -> o.getAlias().equals(alias)).findAny().get().getId();
	}

}
