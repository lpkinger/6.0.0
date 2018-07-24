package com.uas.erp.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Exporters {
	private Exporters() {

	}

	public static Map<String, Long> exporters = new ConcurrentHashMap<String, Long>(3);
}
