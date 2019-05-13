package com.linkstec.mock.excel.v2;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.linkstec.mock.excel.vo.RelationFieldMap;
import com.linkstec.mock.excel.vo.RelationFieldMapList;
import com.linkstec.mock.excel.vo.TableInfo;
import com.linkstec.mock.excel.vo.v2.Case;
import com.linkstec.mock.excel.vo.v2.Condition;
import com.linkstec.mock.excel.vo.v2.ExcelMetaData;
import com.linkstec.mock.mapper.TableInfoMapper;
import com.linkstec.mock.utils.ConditionRightPartHolder;
import com.linkstec.mock.utils.ExcelUtils;
import com.linkstec.mock.utils.MasterMapperExecutor;
import com.linkstec.mock.utils.MockUtils;
import com.linkstec.mock.vo.ColumnVo;

/**
 *
 * @author pan yilin
 *
 */
@Component("writer2")
public class ExcelWriter {

	//private Logger logger = LoggerFactory.getLogger(ExcelWriter.class);

	@Autowired
	private TableInfoMapper tableInfoMapper;

	@Autowired
	private MasterMapperExecutor executor;

	public void write(ExcelMetaData data, String output) throws Exception {

		int rowIndex = 1;
		List<TableInfo> tables = data.getTables();
		List<Case> cases = data.getCaze();

		try (
			XSSFWorkbook wb = new XSSFWorkbook();
			FileOutputStream out = new FileOutputStream(output);){

			XSSFCellStyle borderStyle = ExcelUtils.getStyleBorder(wb);
			XSSFCellStyle colorStyle = ExcelUtils.getStyleBorderAndColor(wb);
			XSSFSheet sheet = wb.createSheet("データ");// 创建Excel工作表对象

			RelationFieldMapList relations = new RelationFieldMapList();

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
					int count = testCase.getRecordsCount();
 					HashMap<String, String> conditionList = new HashMap<>();
					List<Condition> conditions = testCase.getConditions();

					for(Condition c : conditions) {
						//过滤 ＝右侧 表达式 字段表达式　即　（ａ．ｆｉｅｌｄ　＝　ｂ．ｆｉｅｌｄ）
						if(MockUtils.isField(c.getLeft()) && MockUtils.isField(c.getRight())) {
							for(int x = 0; x<count; x++) {
								RelationFieldMap m = new RelationFieldMap(String.format("%s.%d", testCase.getCaseNumber(), x), c.getLeft(), c.getRight());
								if(!relations.isExists(m)) {
									relations.add(m);
								}
							}

						}
						//过滤 ＝右侧 表达式 非  字段表达式　即　（ａ．ｆｉｅｌｄ　＝　ａｎｙ　ｅｘｃｅｐｔ　ｂ．ｆｉｅｌｄ）
						else if(MockUtils.isField(c.getLeft()) && !MockUtils.isField(c.getRight())) {
							conditionList.put(c.getLeft(), c.getRight());
						}
						//过滤 ＝右侧 表达式 非  字段表达式　即　（ａ．ｆｉｅｌｄ　＝　ａｎｙ　ｅｘｃｅｐｔ　ｂ．ｆｉｅｌｄ）
						else if(!MockUtils.isField(c.getLeft()) && MockUtils.isField(c.getRight())) {
							conditionList.put(c.getRight(), c.getLeft());
						}
					}

					for (int k = 0; k < count; k++) {
						int cols = 1;
						XSSFRow colRow = sheet.createRow(rowIndex);
						for (String type : typeList) {
							String[] arr = type.split("\\|");
							String field = StringUtils.trim(arr[0]);
							String typeStr = StringUtils.trim(arr[1]);
							String isIdentity = arr[2];
							String isPk = arr[3];
							String value="";
							String currentField = String.format("%s.%s", tableAlias, field);
							List<RelationFieldMap> lst = relations.getRelationFieldMapsBy(String.format("%s.%d", testCase.getCaseNumber(), k), currentField);
							// 1. pkContainer中查找　ａｌｉａｓ．ｆｉｅｌｄ　生成主键ｖａｌｕｅ
							if (!lst.isEmpty()) {
								for(RelationFieldMap map : lst) {
									if(StringUtils.isNotBlank(map.getValue())) {
										value = map.getValue();
										break;
									}
								}
								//如果是主键 并且无值
								if(StringUtils.isBlank(value) && "1".equals(isPk)) {
									value = System.nanoTime()+"";
									//将主键值存入某个容器中　ｐｋＣｏｎｔａｉｎｅｒ．
									for(RelationFieldMap map : lst) {
										map.setValue(value);
									}
								}else {
									// 获取随机ｖａｌｕｅ
									//logger.info("获取随机ｖａｌｕｅ from 第一个if");
									//value = MockUtils.getValue(typeStr, dataType);
								}
							}
							//非主键，并有条件的valuｅ　处理
							else if (conditionList.get(tableAlias + "." + field) != null) {
								value = new ConditionRightPartHolder(executor, conditionList.get(tableAlias + "." + field))
										.get();
							}else {
								// 获取随机ｖａｌｕｅ
								value = MockUtils.getValue(typeStr, testCase.getType());
							}
							colRow.createCell(cols).setCellValue(value);
							colRow.getCell(cols).setCellStyle(borderStyle);
							cols++;
						}
						rowIndex++;
					}
					if(count > 1 ) {
						sheet.addMergedRegion(new CellRangeAddress(rowIndex - count, rowIndex - 1, 0, 0));
						sheet.getRow(rowIndex - count).createCell(0).setCellValue(testCase.getCaseNumber());
						sheet.getRow(rowIndex - count).getCell(0).setCellStyle(borderStyle);
					}else {
						Row singleRow = sheet.getRow(rowIndex - 1);
						Cell c =singleRow.createCell(0);
						c.setCellValue(testCase.getCaseNumber());
						c.setCellStyle(borderStyle);
					}
				}
				rowIndex++;
			}



			out.flush();
			wb.write(out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
