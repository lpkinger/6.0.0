package com.uas.erp.dao.util;

import java.util.concurrent.TimeUnit;

/**
 * 结果消费者
 * 
 * @author yingp
 *
 * @param <T>
 */
public class ResultConsumer<T> implements Runnable {

	private final ResultQueue<T> resultQueue;

	private final ResultProcesser<T> processer;

	/**
	 * @param resultQueue
	 * @param processer
	 *            处理单个result
	 */
	public ResultConsumer(ResultQueue<T> resultQueue, ResultProcesser<T> processer) {
		this.resultQueue = resultQueue;
		this.processer = processer;
	}

	@Override
	public void run() {
		while (!resultQueue.isClose() || !resultQueue.getQueue().isEmpty()) {
			try {
				T result = resultQueue.getQueue().poll(10, TimeUnit.MILLISECONDS);
				if (result != null)
					try {
						processer.processResult(result);
					} catch (Exception e) {
						e.printStackTrace();
						// 出错情况下示意中断
						resultQueue.close();
						// queue剩余结果也不再处理
						break;
					}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		resultQueue.close();
	}

}
