package com.uas.erp.dao;

/**
 * 
 */
public class SpObserver {

	private static ThreadLocal<String> prev = new InheritableThreadLocal<String>();
	private static ThreadLocal<String> local = new InheritableThreadLocal<String>();

	/**
	 * 切换数据源
	 * 
	 * @param sp
	 *            dbsource name
	 */
	public static void putSp(String sp) {
		prev.set(getSp());
		local.set(sp);
	}

	public static String getSp() {
		return (String) local.get();
	}

	public static void clear() {
		prev.set(null);
		local.set(null);
	}

	/**
	 * 切换回之前数据源
	 */
	public static void back() {
		local.set(prev.get());
		prev.set(null);
	}
}