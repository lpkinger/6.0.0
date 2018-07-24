package com.uas.erp.dao.util;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.uas.erp.dao.SpObserver;

/**
 * 通过{@code ResultQueue}实现生产、使用异步进行
 * 
 * @see ResultQueue
 * @author yingp
 * 
 */
public class ResultQueueUtils {

	public static <T> void execute(ResultQueue<T> queue, ResultProducer<T> producer, ResultConsumer<T> consumer) {
		int size = 2;
		ExecutorService pool = Executors.newFixedThreadPool(size);
		CompletionService<Void> service = new ExecutorCompletionService<Void>(pool);
		service.submit(producer, null);
		service.submit(consumer, null);
		pool.shutdown();
		int count = 0;
		long start = System.currentTimeMillis();
		int timeout = 300000;// 超时设置，5 min
		while (count < size) {
			Future<Void> future = service.poll();
			if (future != null) {
				count++;
			}
			if (count < size) {
				if (System.currentTimeMillis() - start > timeout) {
					producer.stop();
					consumer.stop();
					// stop后不立即break，可能队列里面还有consumer未处理的
				}
				try {
					TimeUnit.MILLISECONDS.sleep(50);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public static <T> void execute(JdbcTemplate jdbcTemplate, String executable, RowMapper<T> rowMapper, ResultProcesser<T> processer) {
		ResultQueue<T> queue = new ResultQueue<T>();
		ResultProducer<T> producer = new ResultProducer<T>(jdbcTemplate, SpObserver.getSp(), executable, queue, rowMapper);
		ResultConsumer<T> consumer = new ResultConsumer<T>(queue, processer);
		execute(queue, producer, consumer);
	}

	public static void execute(JdbcTemplate jdbcTemplate, String executable, MapResultProcesser processer) {
		MapResultQueue queue = new MapResultQueue();
		execute(queue, new MapResultProducer(jdbcTemplate, SpObserver.getSp(), executable, queue), new MapResultConsumer(queue, processer));
	}
}
