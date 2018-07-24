package com.uas.erp.dao.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 封装一个生产者-使用者队列
 * 
 * @author yingp
 *
 * @param <T>
 */
public class ResultQueue<T> {

	private BlockingQueue<T> queue = new LinkedBlockingQueue<T>();

	private final AtomicBoolean closed = new AtomicBoolean(false);

	/**
	 * producer、 consumer双方均可示意结束
	 */
	public void close() {
		closed.set(true);
	}

	public boolean isClose() {
		return closed.get();
	}

	public BlockingQueue<T> getQueue() {
		return queue;
	}

}
