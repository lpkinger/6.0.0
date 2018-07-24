package com.uas.erp.core;

public class ClassUtil {

	public static Class<?>[] getObjectsClasses(Object[] values) {
		Class<?>[] classes = new Class[values.length];
		for (int i = 0, j = values.length; i < j; i++) {
			if (null != values[i])
				classes[i] = values[i].getClass();
			else
				classes[i] = String.class;
		}
		return classes;
	}

}
