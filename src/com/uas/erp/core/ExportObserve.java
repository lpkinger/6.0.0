package com.uas.erp.core;

import java.util.Observable;
import java.util.Set;

public final class ExportObserve extends Observable {
	private static final ExportObserve instance = new ExportObserve();
	public static final int warnSize = 5000;// 超过警告条数将写入exports
	static final int maxSize = 2;// 最多允许同时执行导出动作
	static final int delay = 20000;// 20s之后视作无效

	public static ExportObserve getInstance() {
		return instance;
	}

	public void putObserve(String sid) {
		Exporters.exporters.put(sid, System.currentTimeMillis());
	}

	private synchronized void refresh() {
		Set<String> heaps = Exporters.exporters.keySet();
		Long now = System.currentTimeMillis();
		for (String heap : heaps) {
			if (Exporters.exporters.get(heap) + delay <= now)
				Exporters.exporters.remove(heap);
		}
	}

	public int size() {
		refresh();
		return Exporters.exporters.size();
	}

	public void remove(String sid) {
		Exporters.exporters.remove(sid);
	}
}
