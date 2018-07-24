package com.uas.erp.core;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 数值处理工具
 * 
 * @author yingp
 * 
 */
public class NumberUtil {

	/**
	 * 是否为空、0
	 * 
	 * @param numberObj
	 * @return
	 */
	public static boolean isEmpty(Object numberObj) {
		try {
			return numberObj == null || "0".equals(String.valueOf(numberObj)) || Integer.parseInt(numberObj.toString()) == 0;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * format double类型
	 * 
	 * @param number
	 *            需要format的数据
	 * @param f
	 *            保留f位的小数
	 * @return format之后的double类型的数据
	 */
	public static double formatDouble(double number, int f) {
		if (f > 0) {
			BigDecimal b = new BigDecimal(Double.toString(number));
			BigDecimal one = new BigDecimal("1");
			return b.divide(one, f, BigDecimal.ROUND_HALF_UP).doubleValue();
		} else {
			return Math.floor(number);
		}
	}

	/**
	 * format double类型
	 * 
	 * @param number
	 *            需要format的数据
	 * @param f
	 *            保留f位的小数
	 * @return format之后的double类型的数据
	 */
	public static double formatDouble(String number, int f) {
		double n = Double.parseDouble(number);
		if (f > 0) {
			BigDecimal b = new BigDecimal(number);
			BigDecimal one = new BigDecimal("1");
			return b.divide(one, f, BigDecimal.ROUND_HALF_UP).doubleValue();
		} else {
			return Math.floor(n);
		}
	}

	/**
	 * 浮点型转成BigDecimal
	 * 
	 * @param number
	 * @return
	 */
	public static String parseBigDecimal(double number) {
		int scale = BigDecimal.valueOf(number).scale();
		if (scale == -1) {
			scale = 0;
		}
		return String.valueOf(BigDecimal.valueOf(number).setScale(scale, BigDecimal.ROUND_HALF_UP));
	}

	/**
	 * 数字格式化
	 * 
	 * @param number
	 * @param f
	 * @return
	 */
	public static String formatNumber(Object number, int f) {
		if ("0".equals(String.valueOf(number)))
			return "0";
		if (number instanceof String)
			number = Double.parseDouble(number.toString());
		DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
		df.setGroupingSize(3);
		int len = number.toString().length();
		if (number.toString().indexOf(".") > 0)
			len = number.toString().indexOf(".");
		String pattern = len > 3 ? "0,000" : "0";
		for (int i = 0; i < f; i++) {
			if (i == 0)
				pattern += ".";
			pattern += "0";
		}
		df.applyPattern(pattern);
		try {
			return df.format(number);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 设置小数点保留位数
	 * */
	public static float subFloat(float f, int length) {
		String fStr = String.valueOf(f);
		int i = fStr.indexOf(".");
		String returnStr = null;
		if (fStr.length() > i + 1 + length) {
			returnStr = fStr.substring(0, i + 1 + length);
		} else
			returnStr = fStr;
		float returnf = (Float.valueOf(returnStr)).floatValue();
		return returnf;
	}

	/**
	 * int数组转化成Integer数组
	 */
	public static Integer[] toIntegerArray(int[] arr) {
		int n = arr.length;
		Integer[] iarr = new Integer[n];
		for (int i = 0; i < n; i++) {
			iarr[i] = new Integer(arr[i]);
		}
		return iarr;
	}

	public static Number nvl(Number number, Number ifNullNumber) {
		return number == null ? ifNullNumber : number;
	}

	public static int compare(Double paramDouble1, Double paramDouble2) {
		if (paramDouble1 == null)
			paramDouble1 = 0.0;
		if (paramDouble2 == null)
			paramDouble2 = 0.0;
		return Double.compare(paramDouble1, paramDouble2);
	}

	/**
	 * Double型加法运算
	 * 
	 * @param d1
	 *            第一个加数
	 * @param ds
	 *            若干个加数
	 * @return 相加的结果
	 * @author suntg
	 */
	public static double add(Double d1, Double... ds) {
		BigDecimal bd = new BigDecimal(Double.toString(d1));
		for (Double d : ds) {
			bd = bd.add(new BigDecimal(Double.toString(d)));
		}
		return bd.doubleValue();
	}

	/**
	 * Double型加法运算
	 * 
	 * @param d1
	 *            第一个加数
	 * @param ds
	 *            若干个加数
	 * @return 相加的结果
	 * @author suntg
	 */
	public static double add(String ds1, String... dss) {
		BigDecimal bd = new BigDecimal(ds1);
		for (String ds : dss) {
			bd = bd.add(new BigDecimal(ds));
		}
		return bd.doubleValue();
	}

	/**
	 * Double型减法运算
	 * 
	 * @param d1
	 *            被减数
	 * @param d2
	 *            减数
	 * @return 运算结果
	 * @author suntg
	 */
	public static double sub(Double d1, Double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * Double 型乘法运算
	 * 
	 * @param d1
	 *            第一个乘数
	 * @param d2
	 *            第二个乘数
	 * @return 运算结果
	 * @author suntg
	 */
	public static double mul(Double d1, Double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * Double 型除法运算
	 * 
	 * @param d1
	 *            被除数
	 * @param d2
	 *            除数
	 * @param scale
	 *            小数点四舍五入精度
	 * @return 运算结果
	 * @author suntg
	 */
	public static double div(Double d1, Double d2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("四舍五入精度必须大于0");
		}
		if (d2.doubleValue() == 0) {
			throw new IllegalArgumentException("被除数不能为0");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 默认小数点四舍五入精度为6的Double型除法运算
	 * 
	 * @param d1
	 *            被除数
	 * @param d2
	 *            除数
	 * @return 运算结果
	 * @author suntg
	 */
	public static double div(Double d1, Double d2) {
		return div(d1, d2, 6);
	}

	/**
	 * 比较浮点型数值
	 * 
	 * @param paramDouble1
	 * @param paramDouble2
	 * @param paramInt
	 *            精度
	 * @return
	 */
	public static int compare(double paramDouble1, double paramDouble2, int paramInt) {
		return Double.compare(formatDouble(paramDouble1, paramInt), formatDouble(paramDouble2, paramInt));
	}

}
