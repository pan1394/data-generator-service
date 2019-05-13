package com.linkstec.mock.processor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

//import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
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
import com.linkstec.mock.mapper.TableInfoMapper;
import com.linkstec.mock.utils.ConditionRightPartHolder;
import com.linkstec.mock.utils.ExcelUtils;
import com.linkstec.mock.utils.MasterMapperExecutor;
import com.linkstec.mock.utils.MockUtils;
import com.linkstec.mock.vo.ColumnVo;
import com.linkstec.mock.vo.Result;

@Component
public class MockData4DB {

  @Autowired
  private TableInfoMapper tableInfoMapper;

  @Autowired
  private MasterMapperExecutor executor;

  public void MockData(String input, String output) {
    try {
      //1、获取文件输入流
      InputStream inputStream = new FileInputStream(input);
      //2、获取Excel工作簿对象
      XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
      //3、得到Excel工作表对象
      XSSFSheet sheetAt = workbook.getSheetAt(0);
      HashMap<String, String> tables = new HashMap<String, String>();
      int i = 9;
      for (;; i++) {
        Row row = sheetAt.getRow(i);
        if (row == null) {
          break;
        }
        tables.put(row.getCell(0).getStringCellValue(), row.getCell(2).getStringCellValue());
      }

      i = i + 3;
      Row row = sheetAt.getRow(i);
      int count = Integer.parseInt(row.getCell(2).getStringCellValue());
      i++;
      row = sheetAt.getRow(i);
      String Type = row.getCell(2).getStringCellValue();

      i = i + 2;
      List<Case> cases = new ArrayList<Case>();
      for (;; i++) {
        row = sheetAt.getRow(i);
        if (row == null) {
          break;
        }
        Case testCase = new Case();
        List<Condition> conditisons = new ArrayList<Condition>();
        Result result = ExcelUtils.isMergedRegion(sheetAt, i, 0);
        testCase.setCaseNumber(ExcelUtils.getMergedRegionCellValue(sheetAt, result));
        int end = result.endRow;

        while (i < end) {
          Condition condition = new Condition();
          condition.setLeft(row.getCell(1).getStringCellValue());
          condition.setSymbol(row.getCell(2).getStringCellValue());
          condition.setRight(row.getCell(3).getStringCellValue());
          conditisons.add(condition);
          i++;
          row = sheetAt.getRow(i);
        }
        testCase.setConditions(conditisons);
        cases.add(testCase);
      }

      //out put
      XSSFWorkbook wb = new XSSFWorkbook();//创建Excel工作簿对象
      XSSFSheet sheet = wb.createSheet("データ");//创建Excel工作表对象
      XSSFCellStyle borderStyle = getStyleBorder(wb);
      XSSFCellStyle colorStyle = getStyleBorderAndColor(wb);

      i = 1;
      HashMap<String, List<String>> pkList = new HashMap<>();
      HashMap<String, String> fkList = new HashMap<>();
      for (Map.Entry<String, String> entry : tables.entrySet()) {
        sheet.addMergedRegion(new CellRangeAddress(i, i + 3, 0, 0));
        row = sheet.createRow(i);
        row.createCell(0).setCellValue("No");
        row.getCell(0).setCellStyle(colorStyle);
        row.createCell(1).setCellValue("DB状態");
        row.getCell(1).setCellStyle(colorStyle);
        sheet.createRow(i + 1).createCell(1).setCellValue(entry.getKey());
        sheet.getRow(i + 1).getCell(1).setCellStyle(colorStyle);
        //sqlserver
        List<ColumnVo> columns = tableInfoMapper.getColumns(entry.getKey());
        List<String> typeList = new ArrayList<>();
        int col = 1;
        XSSFRow fieldName = sheet.createRow(i + 2);
        XSSFRow extraNmae = sheet.createRow(i + 3);
        for (ColumnVo rs : columns) {
          fieldName.createCell(col).setCellValue(rs.getName());
          fieldName.getCell(col).setCellStyle(colorStyle);
          extraNmae.createCell(col).setCellValue(rs.getValue());
          extraNmae.getCell(col).setCellStyle(colorStyle);
          typeList.add(rs.getType());
          col++;
        }
        for (int a = 2; a <= typeList.size(); a++) {
          sheet.getRow(i).createCell(a).setCellStyle(colorStyle);
          sheet.getRow(i + 1).createCell(a).setCellStyle(colorStyle);
        }
        i = i + 4;
        for (Case testCase : cases) {
          HashMap<String, String> conditionList = new HashMap<>();
          List<Condition> conditions = testCase.getConditions();

          for (Condition c : conditions) {
            if (c.getLeft().indexOf(".") != -1 && (c.getLeft().indexOf(entry.getKey() + ".") == 0
                || c.getLeft().indexOf(entry.getValue() + ".") == 0)) {
              if (c.getRight().indexOf(".") != -1 && !StringUtils.isNumeric(c.getRight())) {
                pkList.put(
                    testCase.getCaseNumber() + "|" + entry.getKey() + c.getLeft().substring(c.getLeft().indexOf(".")),
                    new ArrayList<String>());
              }
              if (c.getRight().indexOf(".") == -1) {
                conditionList.put(entry.getKey() + c.getLeft().substring(c.getLeft().indexOf(".")), c.getRight());
              }
            }
            if (c.getRight().indexOf(".") != -1 && (c.getRight().indexOf(entry.getKey() + ".") == 0
                || c.getRight().indexOf(entry.getValue() + ".") == 0)) {
              String tableName = c.getLeft().substring(0, c.getLeft().indexOf("."));
              if (tables.get(tableName) == null) {
                tableName = getKey(tables, tableName);
              }
              int colNum = 0;
              for (; colNum < typeList.size(); colNum++) {
                if (typeList.get(colNum).indexOf(c.getRight().substring(c.getRight().indexOf(".") + 1)) == 0) {
                  break;
                }
              }
              fkList.put(entry.getKey() + c.getRight().substring(c.getRight().indexOf(".")),
                  testCase.getCaseNumber() + "|" + tableName + c.getLeft().substring(c.getLeft().indexOf(".")) + "|" + i
                      + "|" + (1 + colNum));
            }
          }
          for (int k = 0; k < count; k++) {
            int cols = 1;
            XSSFRow colRow = sheet.createRow(i);
            for (String type : typeList) {
              String[] arr = type.split("\\|");
              String field = arr[0];
              String typeStr = arr[1];
              String value;
              if (arr.length > 2 && arr[2].equals("1")) {
                value = Integer.toString(k + 1);
              } else {
                value = MockUtils.getValue(typeStr, Type);
              }
              List<String> pks = pkList.get(testCase.getCaseNumber() + "|" + entry.getKey() + "." + field);
              if (pks != null) {
                pks.add(value);
              }
              if (fkList.get(testCase.getCaseNumber() + "|" + entry.getKey() + "." + field) != null) {
                continue;
              }
              if (conditionList.get(entry.getKey() + "." + field) != null) {
                value = new ConditionRightPartHolder(executor, conditionList.get(entry.getKey() + "." + field)).get();
              }
              colRow.createCell(cols).setCellValue(value);
              colRow.getCell(cols).setCellStyle(borderStyle);
              cols++;
            }
            i++;
          }
          sheet.addMergedRegion(new CellRangeAddress(i - count, i - 1, 0, 0));
          sheet.getRow(i - count).createCell(0).setCellValue(testCase.getCaseNumber());
          sheet.getRow(i - count).getCell(0).setCellStyle(borderStyle);
          i++;
        }

      }
      if (fkList.size() > 0) {
        for (Map.Entry<String, String> entry : fkList.entrySet()) {
          String[] arg = entry.getValue().split("\\|");
          List<String> values = pkList.get(arg[0] + "|" + arg[1]);
          for (int i1 = 0; i1 < count; i1++) {
            sheet.getRow(Integer.parseInt(arg[2]) + i1).createCell(Integer.parseInt(arg[3]))
                .setCellValue(getRandomList(values));
            sheet.getRow(Integer.parseInt(arg[2]) + i1).getCell(Integer.parseInt(arg[3])).setCellStyle(borderStyle);
          }
        }
      }

      FileOutputStream out = new FileOutputStream(output); //向d://test.xls中写数据
      out.flush();
      wb.write(out);
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private XSSFCellStyle getStyleBorder(XSSFWorkbook wb) {
    XSSFCellStyle style = wb.createCellStyle();
    style.setBorderBottom(BorderStyle.THIN); //下边框
    style.setBorderLeft(BorderStyle.THIN);//左边框
    style.setBorderTop(BorderStyle.THIN);//上边框
    style.setBorderRight(BorderStyle.THIN);//右边框
    return style;
  }

  private XSSFCellStyle getStyleBorderAndColor(XSSFWorkbook wb) {
    XSSFCellStyle style = wb.createCellStyle();
    style.setBorderBottom(BorderStyle.THIN); //下边框
    style.setBorderLeft(BorderStyle.THIN);//左边框
    style.setBorderTop(BorderStyle.THIN);//上边框
    style.setBorderRight(BorderStyle.THIN);//右边框
    style.setFillForegroundColor((short) 7);
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    return style;
  }



  private String getRandomList(List<String> list) {
    Random random = new Random();
    int n = random.nextInt(list.size());
    return list.get(n);
  }

  //根据value值获取到对应的一个key值
  public static String getKey(HashMap<String, String> map, String value) {
    String key = null;
    //Map,HashMap并没有实现Iteratable接口.不能用于增强for循环.
    for (String getKey : map.keySet()) {
      if (map.get(getKey).equals(value)) {
        key = getKey;
      }
    }
    return key;

  }
}