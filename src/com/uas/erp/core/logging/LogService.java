package com.uas.erp.core.logging;

import java.util.Collection;

public abstract interface LogService<T extends BufferedLogable> {

	/**
	 * 日志持久化保存
	 * 
	 * @param logable
	 */
	public void save(T logable);

	/**
	 * 日志批量持久化保存
	 * 
	 * @param logables
	 */
	public void save(Collection<T> logables);

}
