package com.linkstec.mock.utils;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.linkstec.mock.vo.Result;

public class ExcelUtils {

	 public static Result isMergedRegion(Sheet sheet, int row, int column) {
		    int sheetMergeCount = sheet.getNumMergedRegions();
		    for (int i = 0; i < sheetMergeCount; i++) {
		      CellRangeAddress range = sheet.getMergedRegion(i);
		      int firstColumn = range.getFirstColumn();
		      int lastColumn = range.getLastColumn();
		      int firstRow = range.getFirstRow();
		      int lastRow = range.getLastRow();
		      if (row >= firstRow && row <= lastRow) {
		        if (column >= firstColumn && column <= lastColumn) {
		          return new Result(true, firstRow + 1, lastRow + 1, firstColumn + 1, lastColumn + 1);
		        }
		      }
		    }
		    return new Result(false, row+1, 0, column+1, 0);
	}

	
	public static String getMergedRegionCellValue(Sheet sheet, Result mergedCellInfo) {
		Cell c = sheet.getRow(mergedCellInfo.startRow-1).getCell(mergedCellInfo.startCol-1);
		return getCellValue(c);
	}

	public static String getCellValue(Cell c) {
		CellType t = c.getCellType();
		switch(t) {
			case NUMERIC:
				return String.valueOf((int)c.getNumericCellValue());
			case STRING:
				return c.getStringCellValue();
			default:
				return "";
		}
	}


	public static XSSFCellStyle getStyleBorder(XSSFWorkbook wb) {
		XSSFCellStyle style = wb.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN); // 下边框
		style.setBorderLeft(BorderStyle.THIN);// 左边框
		style.setBorderTop(BorderStyle.THIN);// 上边框
		style.setBorderRight(BorderStyle.THIN);// 右边框
		return style;
	}

	public static XSSFCellStyle getStyleBorderAndColor(XSSFWorkbook wb) {
		XSSFCellStyle style = wb.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN); // 下边框
		style.setBorderLeft(BorderStyle.THIN);// 左边框
		style.setBorderTop(BorderStyle.THIN);// 上边框
		style.setBorderRight(BorderStyle.THIN);// 右边框
		style.setFillForegroundColor((short) 7);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style;
	}

	public static String getStringValue(Row r, int columnNo) {
		return r.getCell(columnNo).getStringCellValue();
	}

}
