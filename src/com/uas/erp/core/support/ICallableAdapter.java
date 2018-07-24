package com.uas.erp.core.support;

import java.util.concurrent.Callable;

/**
 * 代理callable来执行
 * 
 * <pre>
 * 涉及到一个callable对象调用不同参数循环执行的问题，不能直接在ICallable对象里面设置参数，需要代理执行
 * </pre>
 * 
 * @see ICallable
 * @see Callable
 * 
 * @author yingp
 * 
 * @param <V>
 *            返回结果类型
 * @param <T>
 *            参数类型
 */
public final class ICallableAdapter<V, T> implements Callable<V> {

	private final ICallable<V, T> task;
	private final T param;

	public ICallableAdapter(ICallable<V, T> task, T param) {
		this.task = task;
		this.param = param;
	}

	@Override
	public V call() throws Exception {
		return task.call(param);
	}

}
