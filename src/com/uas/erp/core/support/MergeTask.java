package com.uas.erp.core.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.ClassUtils;

/**
 * 任务聚合<br>
 * 将多个分支任务并行处理，将任务处理的结果合并返回
 * 
 * @author yingp
 * 
 * @param <T>
 *            返回结果类型
 * @param <V>
 *            传递参数类型
 */
public class MergeTask<T, V> {

	// 缓冲的线程池
	private final ExecutorService threadPool;

	private final CompletionService<T> service;

	// 定义一个带参有返回的callable
	private final ICallable<T, V> callable;

	private int taskCount = 0;

	public MergeTask(ICallable<T, V> callable) {
		this.threadPool = Executors.newFixedThreadPool(30);
		this.service = new ExecutorCompletionService<T>(threadPool);
		this.callable = callable;
	}

	/**
	 * 添加分支任务
	 * 
	 * @param param
	 *            针对ICallable传递的参数
	 * @return
	 */
	public MergeTask<T, V> join(V param) {
		service.submit(new ICallableAdapter<T, V>(callable, param));
		taskCount++;
		return this;
	}

	public MergeTask<T, V> join(Collection<V> params) {
		for (V param : params)
			join(param);
		return this;
	}

	/**
	 * 获取聚合的结果，结果扔到List里面返回
	 * 
	 * @return
	 */
	public List<T> execute() {
		threadPool.shutdown();
		int finish = 0;
		List<T> datas = new ArrayList<T>();
		while (finish < taskCount) {
			// 非阻塞方式
			Future<T> future = service.poll();
			if (future != null) {
				try {
					T result = future.get();
					if (result != null) {
						datas.add(result);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				finish++;
			}
			if (finish < taskCount)
				try {
					// 等待50毫秒再进入下一次查找
					TimeUnit.MILLISECONDS.sleep(50);
				} catch (InterruptedException e) {
				}
		}
		return datas;
	}

	/**
	 * 获取聚合的结果（自定义返回类型），结果以List返回
	 * 
	 * <pre>
	 * 主要是如果{@code T}本身是List类型，希望将{@code T}合并为一个List返回
	 * </pre>
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public <S> List<S> execute(Class<?> cls) {
		threadPool.shutdown();
		int finish = 0;
		List<S> datas = new ArrayList<S>();
		while (finish < taskCount) {
			// 非阻塞方式
			Future<T> future = service.poll();
			if (future != null) {
				try {
					T result = future.get();
					if (result != null) {
						// 这里假如返回值本身是List格式的，则全扔进一个List里面
						if (result instanceof List) {
							datas.addAll((List<S>) result);
						} else if (ClassUtils.isAssignable(result.getClass(), cls))
							datas.add((S) result);
					}
				} catch (InterruptedException e) {
				} catch (ExecutionException e) {
				}
				finish++;
			}
			if (finish < taskCount)
				try {
					// 等待50毫秒再进入下一次查找
					TimeUnit.MILLISECONDS.sleep(50);
				} catch (InterruptedException e) {
				}
		}
		return datas;
	}

}
