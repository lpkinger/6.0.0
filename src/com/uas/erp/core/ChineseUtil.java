package com.uas.erp.core;

import java.io.UnsupportedEncodingException;

/**
 * 中文处理工具
 * 
 * @author yingp
 * 
 */
public class ChineseUtil {

	/**
	 * 汉字拼音的首字母,不存在i,u,v
	 */
	private final static int[] HanZiCode = { 0xB0A1, 0xB0C5, 0xB2C1, 0xB4EE,
			0xB6EA, 0xB7A2, 0xB8C1, 0xB9FE, 0xBBF7, 0xBFA6, 0xC0AC, 0xC2E8,
			0xC4C3, 0xC5B6, 0xC5BE, 0xC6DA, 0xC8BB, 0xC8F6, 0xCBFA, 0xCDDA,
			0xCEF4, 0xD1B9, 0xD4D1, 0xD8A0 };
	private final static int LENGTH = HanZiCode.length;

	/**
	 * <p>
	 * 该方法用于获得传入的汉字的首字母
	 * </p>
	 * <p>
	 * 如果传入的word不属于GB2312所包含的汉字，则原样返回
	 * </p>
	 * <p>
	 * 如果传入多个汉字，则原样返回
	 * </p>
	 * 
	 * @param word
	 *            String型值
	 */
	public static String getPinyin(String word) {
		byte[] byte1;
		char c = 'a' - 1;
		try {
			byte1 = word.getBytes("gb2312");
			if (byte1.length == 2) {
				int codeValue = ((byte1[0] + 256) * 256 + byte1[1] + 256);
				if (codeValue >= HanZiCode[0]
						&& codeValue <= HanZiCode[LENGTH - 1]) {
					for (int i = 0; i < LENGTH; i++) {
						if (codeValue >= HanZiCode[i]) {
							if ((c + 1 == 'i')) {
								c += 2;
							} else if (c + 1 == 'u') {
								c += 3;
							} else {
								c++;
							}
						}
					}
					return c + "";
				}
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return word;
	}

	/**
	 * 获得传入字符串中每个汉字拼音的首字母
	 * @param str
	 */
	public static String convertToPinyin(String str) {
		int length = str.length();
		String result = "";
		for (int i = 0; i < length; i++) {
			result += getPinyin(str.substring(i, i + 1));
		}
		return result;
	}
	
	
	/**
	 * 获得传入字符串中第一个汉字拼音的首字母,或第一个英文字母
	 * @param str
	 * @return
	 */
	public static String getFirstLetter(String str) {
		return getPinyin(str.substring(0, 1));
	}
	
}
