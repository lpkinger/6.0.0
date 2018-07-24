package com.uas.erp.core.support;

import java.util.concurrent.Callable;

/**
 * 能传入参数执行的callable
 * 
 * @author yingp
 * @see Callable
 * 
 * @param <V>
 *            返回结果类型
 * @param <T>
 *            参数类型
 */
public abstract interface ICallable<V, T> {

	public abstract V call(T param) throws Exception;

}
