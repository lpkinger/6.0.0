package com.uas.erp.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 字符串操作工具
 * 
 * @author yingp
 * 
 */
public class StringUtil {
	/**
	 * 前台使用escepe之后，java里面解析
	 * 
	 * @param escapeStr
	 * @return
	 */
	public static String unescape(String escapeStr) {
		if (escapeStr != null) {
			return JSONUtil.decodeUnicode(escapeStr.replace("%", "\\"));
		}
		return null;
	}

	/**
	 * 查找字符串重复项
	 * 
	 * @param str
	 * @param ch
	 * @return
	 */
	public static String getRepeats(String str, String ch) {
		Set<String> set = new HashSet<String>();
		String[] datas = str.split(ch);
		StringBuffer repeat = new StringBuffer();
		for (String s : datas) {
			if (s != null && !s.trim().equals("")) {
				if (!set.contains(s)) {
					set.add(s);
				} else {
					if (repeat.length() > 0) {
						repeat.append(",");
					}
					repeat.append(s);
				}
			}
		}
		return repeat.toString();
	}

	/**
	 * 去掉字符串重复项
	 * 
	 * @param str
	 * @param ch
	 * @return
	 */
	public static String deleteRepeats(String str, String ch) {
		Set<String> set = new HashSet<String>();
		String[] datas = str.split(ch);
		StringBuffer repeat = new StringBuffer();
		for (String s : datas) {
			if (s != null && !s.trim().equals("")) {
				if (!set.contains(s)) {
					set.add(s);
					repeat.append(s + ch);
				}
			}
		}
		return repeat.toString().substring(0, repeat.toString().length() - 1);
	}

	/**
	 * InputStream转成字符串
	 * 
	 * @param in
	 * @return
	 */
	public static String parserInputStream(InputStream in) {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringBuffer buffer = new StringBuffer();
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	/**
	 * 字符串是否在数组里面
	 */
	public static boolean isInArray(String[] objs, String str) {
		boolean bool = false;
		for (String obj : objs) {
			if (obj.equals(str)) {
				bool = true;
				break;
			}
		}
		return bool;
	}

	/**
	 * 判断参数是否为空、空字符串、空白格
	 * 
	 * @param object
	 * @return
	 */
	public static boolean hasText(Object object) {
		return object == null ? false : StringUtils.hasText(object.toString());
	}

	/**
	 * @param object
	 * @return 字符串
	 */
	public static String valueOf(Object object) {
		return (object == null) ? null : object.toString();
	}

	/**
	 * 当object为空时，返回nvlValue，否则返回object
	 * 
	 * @param object
	 * @param nvlValue
	 * @return
	 */
	public static String nvl(Object object, String nvlValue) {
		return !hasText(object) ? nvlValue : object.toString();
	}

	/**
	 * 当object为空时，返回nvlValue，否则返回value
	 * 
	 * @param object
	 * @param value
	 * @param nvlValue
	 * @return
	 */
	public static String nvl2(Object object, String value, String nvlValue) {
		return !hasText(object) ? nvlValue : value;
	}

	/**
	 * 类似于String.format的逆运算
	 * 
	 * @param paramString
	 *            待解析字符串
	 * @param pattern
	 *            表达式
	 * @return
	 */
	public static String[] parse(String paramString, String pattern) {
		String[] patternArray = pattern.split("%s");
		int i = 0;
		int strLen = paramString.length();
		int len = patternArray.length;
		int startIndex = 0;
		int endIndex = 0;
		String macher = null;
		String temp = paramString;
		String[] macherArray = new String[pattern.endsWith("%s") ? len : (len - 1)];
		for (String patternStr : patternArray) {
			startIndex += patternStr.length();
			if (startIndex == strLen)
				break;
			temp = paramString.substring(startIndex);
			if (i < len - 1)
				endIndex = startIndex + temp.indexOf(patternArray[i + 1]);
			else
				endIndex = paramString.length();
			macher = paramString.substring(startIndex, endIndex);
			macherArray[i++] = macher;
			if (i == macherArray.length)
				break;
			else
				startIndex = endIndex;
		}
		return macherArray;
	}

	/**
	 * 判断是否包含汉字
	 * 
	 * @param paramString
	 * @return
	 */
	public static boolean hasChinese(String paramString) {
		String regExp = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(regExp);
		return p.matcher(paramString).find();
	}

	final static char[] numbersAndLettersCharArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
			'V', 'W', 'X', 'Y', 'Z' };

	/**
	 * 产生给定长度的随机字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) {
		if (length < 1) {
			return null;
		}
		Random randGen = new Random();
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLettersCharArray[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	/**
	 * 按长度分割字符串
	 *
	 * @param str
	 *            原字符串
	 * @param length
	 */
	public static String[] split(String str, int length) {
		int strLen = str.length();
		int len = (int) Math.ceil((double) strLen / length);
		String[] strArray = new String[len];
		for (int i = 0; i < len; i++) {
			strArray[i] = str.substring(i * length, i < len - 1 ? (i + 1) * length : strLen);
		}
		return strArray;
	}

	/**
	 * 按长度分割字符串
	 * 
	 * @param str
	 *            原字符串
	 * @param length
	 *            切割长度
	 * @param prevStr
	 *            前置字符串
	 * @param subStr
	 *            后置字符串
	 * @param concatStr
	 *            连接字符串
	 * @return
	 */
	public static String splitAndConcat(String str, int length, String prevStr, String subStr, String concatStr) {
		int strLen = str.length();
		int len = (int) Math.ceil((double) strLen / length);
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < len; i++) {
			if (i > 0)
				buffer.append(concatStr);
			buffer.append(prevStr).append(str.substring(i * length, i < len - 1 ? (i + 1) * length : strLen)).append(subStr);
		}
		return buffer.toString();
	}

	/**
	 * 将String[] 数组转为 String
	 * 
	 * @param Str
	 * @return
	 */
	public static String ArraysToString(String[] str) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length; i++) {
			sb.append(str[i]);
		}
		return sb.toString();
	}

	/**
	 * 清除换行空格等
	 * 
	 * @param str
	 * @return
	 */
	public static String trimBlankChars(String str) {
		if (null != str) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			return p.matcher(str).replaceAll("");
		}
		return null;
	}

	/**
	 * 字节数组转base64字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String encodeBase64(byte[] bytes) {
		if (null != bytes) {
			BASE64Encoder encoder = new BASE64Encoder();
			return encoder.encode(bytes);
		}
		return null;
	}

	/**
	 * base64字符串转字节数组
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] decodeBase64(String data) {
		if (null != data) {
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				return decoder.decodeBuffer(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
