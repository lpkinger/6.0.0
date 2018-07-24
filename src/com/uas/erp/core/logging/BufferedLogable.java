package com.uas.erp.core.logging;

public abstract class BufferedLogable {

	protected static final String separator = "#";

	/**
	 * 日志实体转化为字符串
	 * 
	 * @return
	 */
	public abstract String bufferedMessage();

	/**
	 * 字符串转化为日志实体
	 * 
	 * @param bufferedMessage
	 */
	public abstract void bufferedLog(String bufferedMessage);

}
