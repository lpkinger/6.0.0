package com.uas.erp.core.logging;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class BufferedLoggerManager {

	private static Map<String, BufferedLogger> loggers;

	static {
		loggers = new HashMap<String, BufferedLogger>();
	}

	@SuppressWarnings("unchecked")
	public static <T extends BufferedLogger<S>, S extends BufferedLogable> T getLogger(Class<T> cls) {
		String clsName = cls.getName();
		if (loggers.containsKey(clsName)) {
			return (T) loggers.get(clsName);
		}
		try {
			T instance = cls.newInstance();
			loggers.put(clsName, instance);
			return instance;
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return null;
	}

}
