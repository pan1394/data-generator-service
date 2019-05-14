package com.linkstec.mock.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

public class MockUtils {

	static SimpleDateFormat formatDate = new SimpleDateFormat("YYYY/MM/DD");
	static SimpleDateFormat formatDateTime = new SimpleDateFormat("YYYY/MM/DD HH:MM:SS");

	public static String removeParent(String string) {
		if(StringUtils.isBlank(string)) return "";
		if (string.charAt(0) == '(' && string.charAt(string.length() - 1) == ')') {
			string = string.substring(1, string.length() - 1);
		}
		return string;
	}

	public static String getValue(String fieldType, String dataType) {
		String result = "";
		switch (dataType) {
		case "最小値":
			if (fieldType.indexOf("int") != -1 || fieldType.indexOf("bit") != -1 || fieldType.indexOf("tinyint") != -1
					|| fieldType.indexOf("numric") != -1 || fieldType.indexOf("smallint") != -1
					|| fieldType.indexOf("bigint") != -1 || fieldType.indexOf("decimal") != -1
					|| fieldType.indexOf("money") != -1 || fieldType.indexOf("smallmoney") != -1) {
				result = "0";
			}
			if (fieldType.indexOf("date") != -1) {
				Date date = randomDate("1970/01/01", "2050/01/01");
				result = formatDate.format(date);
			}
			if (fieldType.indexOf("datetime") != -1) {
				Date date = randomDate("1970/01/01", "2050/01/01");
				result = formatDateTime.format(date);
			}
			if (fieldType.indexOf("char") != -1) {
				int number = getNumber(fieldType);
				result = RandomString.generateString(number);
			}
			break;
		case "最大値":
			if (fieldType.indexOf("int") != -1 || fieldType.indexOf("bit") != -1 || fieldType.indexOf("tinyint") != -1
					|| fieldType.indexOf("numric") != -1 || fieldType.indexOf("smallint") != -1
					|| fieldType.indexOf("bigint") != -1 || fieldType.indexOf("decimal") != -1
					|| fieldType.indexOf("money") != -1 || fieldType.indexOf("smallmoney") != -1) {
				result = Integer.toString(Integer.MAX_VALUE);
			}
			if (fieldType.indexOf("bit") != -1) {
				result = "1";
			}
			if (fieldType.indexOf("tinyint") != -1) {
				result = "255";
			}
			if (fieldType.indexOf("smallint") != -1) {
				result = Integer.toString((1 << 15) - 1);
			}
			if (fieldType.indexOf("bigint") != -1) {
				result = Long.toString(Long.MAX_VALUE);
			}
			if (fieldType.indexOf("decimal") != -1) {
				result = getMaxBigDecimal(fieldType.substring(7));
			}
			if (fieldType.indexOf("numric") != -1) {
				result = getMaxBigDecimal(fieldType.substring(6));
			}

			if (fieldType.indexOf("date") != -1) {
				Date date = randomDate("1970/01/01", "2050/01/01");
				result = formatDate.format(date);
			}
			if (fieldType.indexOf("datetime") != -1) {
				Date date = randomDate("1970/01/01", "2050/01/01");
				result = formatDateTime.format(date);
			}
			if (fieldType.indexOf("char") != -1) {
				int number = getNumber(fieldType);
				result = RandomString.generateString(number);
			}
			break;
		case "普通":
			if (fieldType.indexOf("int") != -1) {
				result = Long.toString(random(0, Integer.MAX_VALUE));
			}
			if (fieldType.indexOf("bit") != -1) {
				result = Long.toString(random(0, 1));
			}
			if (fieldType.indexOf("tinyint") != -1) {
				result = Long.toString(random(0, 255));
			}
			if (fieldType.indexOf("smallint") != -1) {
				result = Long.toString(random(0, (1 << 15) - 1));
			}
			if (fieldType.indexOf("bigint") != -1) {
				result = Long.toString(random(0, Long.MAX_VALUE));
			}
			if (fieldType.indexOf("decimal") != -1) {
				result = getRandomBigDecimal(fieldType.substring(7));
			}
			if (fieldType.indexOf("numric") != -1) {
				result = getRandomBigDecimal(fieldType.substring(6));
			}

			if (fieldType.indexOf("date") != -1) {
				Date date = randomDate("1970/01/01", "2050/01/01");
				result = formatDate.format(date);
			}
			if (fieldType.indexOf("datetime") != -1) {
				Date date = randomDate("1970/01/01", "2050/01/01");
				result = formatDateTime.format(date);
			}
			if (fieldType.indexOf("char") != -1) {
				int number = getNumber(fieldType);
				result = RandomString.generateString(number);
			}
			break;
		default:
			result = "";
		}
		return result;
	}

	private static String getRandomBigDecimal(String substring) {
		String tmp = removeParent(substring);
		String[] array = tmp.split(",");
		int precision = Integer.valueOf(array[0]);
		int scale = Integer.valueOf(array[1]);
		int integer = precision - scale;
		Random rand = new Random();
		scale = rand.nextInt(scale + 1);
		integer = rand.nextInt(integer + 1);
		StringBuilder sb = new StringBuilder();
		if (integer == 0) {
			sb.append(0);
		}
		while (integer-- > 0) {
			sb.append(rand.nextInt(10));
		}
		sb.append(".");
		if (scale == 0) {
			sb.append(0);
		}
		while (scale-- > 0) {
			sb.append(rand.nextInt(10));
		}
		return sb.toString();
	}

	public static String getMaxBigDecimal(String string) {
		String tmp = removeParent(string);
		String[] array = tmp.split(",");
		int precision = Integer.valueOf(array[0]);
		int scale = Integer.valueOf(array[1]);
		int integer = precision - scale;
		StringBuilder sb = new StringBuilder();
		while (integer-- > 0) {
			sb.append("9");
		}
		sb.append(".");
		while (scale-- > 0) {
			sb.append("9");
		}
		return sb.toString();

	}

	public static Date randomDate(String beginDate, String endDate) {
		try {

			java.util.Date start = formatDate.parse(beginDate);
			java.util.Date end = formatDate.parse(endDate);

			if (start.getTime() >= end.getTime()) {
				return null;
			}

			long date = random(start.getTime(), end.getTime());

			return new Date(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static long random(long begin, long end) {
		long rtn = begin + (long) (Math.random() * (end - begin));
		if (rtn == begin || rtn == end) {
			return random(begin, end);
		}
		return rtn;
	}

	private static int getNumber(String gSQL) {
		String quStr = gSQL.substring(gSQL.indexOf("(") + 1, gSQL.indexOf(")"));
		if ("".equals(quStr)) {
			return 0;
		}
		return Integer.parseInt(quStr);
	}


	/**
	 * 判断字符串是否是某表的字段， 如是返回 true
	 * @param string
	 * @return
	 */
	public static boolean isField(String string) {
		return string.indexOf(".") != -1;
	}

	private static AtomicInteger count = new AtomicInteger();

	public static String getQualityNum(String s, String groupID, int end) {
		count.set(end);
		count.incrementAndGet();
		Integer i = count.get();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		// 将传入的前缀与项目部以及年拼接在以一起
		String format = String.format("%s%s%d", s, groupID, year);
		String strNum = String.format("%d", 10000 + i);
		// 将1截取下来得到剩余的
		String substring = strNum.substring(1);
		return String.format("%s%s", format, substring);
	}

}
