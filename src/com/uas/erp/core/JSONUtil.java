package com.uas.erp.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONUtil {

	static final String Unicode_Pattern = "(\\\\u(\\p{XDigit}{4}))";

	public static String decodeUnicode(String jsonStr) {
		if (jsonStr != null) {
			// unicode格式的转化成汉字
			Pattern pattern = Pattern.compile(Unicode_Pattern);
			Matcher matcher = pattern.matcher(jsonStr);
			char ch;
			while (matcher.find()) {
				ch = (char) Integer.parseInt(matcher.group(2), 16);
				jsonStr = jsonStr.replace(matcher.group(1), ch + "");
			}
		}
		return jsonStr;
	}

	public static Map<Object, Object> toMap(String jsonStr) {
		jsonStr = decodeUnicode(jsonStr);
		jsonStr = jsonStr.substring(jsonStr.indexOf("{") + 1, jsonStr.lastIndexOf("}"));
		String[] strs;
		if(jsonStr.indexOf("\",\"")>0) strs = jsonStr.split(",\"");
		else  strs = jsonStr.split(","); //字段名前没有引号的jsonStr ,类似bom校验：{bo_version:"V1.0",bo_mothercode:"BS00012",bo_ispast:"否",}
		String field = null;
		String value = null;
		Map<Object, Object> map = new HashMap<Object, Object>();
		for (String str : strs) {
			if (str.indexOf(":") > 0) {
				field = str.substring(0, str.indexOf(":"));
				if (field != null) {
					if (field.startsWith("\"")) {
						field = field.substring(1, field.length());
					}
					if (field.endsWith("\"")) {
						field = field.substring(0, field.lastIndexOf("\""));
					}
				}
				value = str.substring(str.indexOf(":") + 1);
				if (value != null) {
					if (value.startsWith("\"")) {
						value = value.substring(1, value.length());
					}
					if (value.endsWith("\"")) {
						value = value.substring(0, value.lastIndexOf("\""));
					}
				}
				map.put(field, value);
			}
		}
		return map;
	}
	
	public static List<Map<Object, Object>> toMapList(String jsonStr) {
		if (jsonStr != null) {
			if (jsonStr.startsWith("[")) {
				jsonStr = jsonStr.substring(1, jsonStr.length());
			}
			if (jsonStr.endsWith("]")) {
				jsonStr = jsonStr.substring(0, jsonStr.lastIndexOf("]"));
			}
			List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
			if (jsonStr.indexOf("},") > -1) {
				String[] js = jsonStr.split("},");
				for (String j : js) {
					if (!j.endsWith("}"))
						j = j + "}";
					list.add(toMap(j));
				}
			} else if (jsonStr.indexOf("{") > -1 && jsonStr.indexOf("}") > -1) {
				list.add(toMap(jsonStr));
			}
			return list;
		}
		return null;
	}
}
