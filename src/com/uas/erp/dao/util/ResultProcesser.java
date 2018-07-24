package com.uas.erp.dao.util;

/**
 * 单个结果处理（不提供处理后返回）
 * 
 * @author yingp
 *
 * @param <T>
 *            生成者提供的队列数据类型
 */
public abstract interface ResultProcesser<T> {

	public abstract void processResult(T param) throws Exception;

}
