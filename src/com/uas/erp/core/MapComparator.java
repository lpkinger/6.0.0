package com.uas.erp.core;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * List<Map<Object, Object>>排序
 * 
 * @author yingp
 * 
 */
public class MapComparator implements Comparator<Map<?, ?>> {

	public static final int ASC = 0;
	public static final int DESC = 1;
	private static final String NUM_REG = "^-?[0-9]+(.[0-9]+)?";

	private Map<String, Integer> fields;// sort by fields

	public MapComparator(String... fields) {
		this.fields = new HashMap<String, Integer>();
		for (String s : fields) {
			this.fields.put(s, ASC);
		}
	}

	public MapComparator(Map<String, Integer> fields) {
		this.fields = fields;
	}

	public MapComparator(String field, int type) {
		this.fields = new HashMap<String, Integer>();
		this.fields.put(field, type);
	}

	public MapComparator(String field, int type, String field1, int type1) {
		this.fields = new HashMap<String, Integer>();
		this.fields.put(field, type);
		this.fields.put(field1, type1);
	}

	@Override
	public int compare(Map<?, ?> a, Map<?, ?> b) {
		int flag = 0;
		if (fields != null) {
			Set<String> keys = fields.keySet();
			for (String k : keys) {
				Object obj1 = a.get(k);
				Object obj2 = b.get(k);
				if (obj1 != null && obj2 != null) {
					flag = compare(String.valueOf(obj1), String.valueOf(obj2), fields.get(k));
					if (flag != 0)
						break;
				}
			}
		}
		return flag;
	}
	
	private int compare(String str1, String str2, int type) {
		int flag = 0;
		if(str1.matches(NUM_REG) && str2.matches(NUM_REG)) {
			if(type == ASC)
				flag = Double.parseDouble(str1) < Double.parseDouble(str2) ? 0 : 1;
			else
				flag = Double.parseDouble(str1) > Double.parseDouble(str2) ? 0 : 1;
		} else {
			if(type == ASC)
				flag = str1.compareTo(str2);
			else
				flag = str2.compareTo(str1);
		}
		return flag;
	}
}
