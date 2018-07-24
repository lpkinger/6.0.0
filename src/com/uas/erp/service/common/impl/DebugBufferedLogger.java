package com.uas.erp.service.common.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.PathUtil;
import com.uas.erp.core.logging.BufferedLogger;
import com.uas.erp.model.DebugLog;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.DebugLogService;

/**
 * <p>
 * debug异步日志记录的工具
 * </p>
 * <strong>步骤：</strong>
 * <ol>
 * <li>请求开始时，记录本次请求到内存中，状态0；</li>
 * <li>请求正常结束时，从内存获取本次请求，状态改为99；</li>
 * <li>请求被异常捕获后，从内存获取本次请求，状态改为-1；</li>
 * <li>处理完成后，日志写到临时文件（可达到每秒百万级的写入速度），内存里面删除本次请求；</li>
 * <li>task执行30秒一次的轮询，将日志文件的内容批量写到数据库</li>
 * <li>task轮询同时，检查内存里面的超时请求记录，状态改为-99，并写入临时文件里面</li>
 * </ol>
 * 
 * @author yingp
 *
 */
public class DebugBufferedLogger extends BufferedLogger<DebugLog> {

	public final static String debugAttribute = "_debug_id";

	private final Map<String, DebugLog> logs;
	private final Map<String, Thread> threads;

	public DebugBufferedLogger() {
		super(PathUtil.getFilePath(), DebugLog.class, ContextUtil.getBean(DebugLogService.class));
		logs = new ConcurrentHashMap<String, DebugLog>();
		threads = new ConcurrentHashMap<String, Thread>();
	}

	/**
	 * 记录debug日志
	 * 
	 * @param request
	 *            本次请求
	 * @param employee
	 *            操作人员
	 */
	public void log(HttpServletRequest request, Employee employee, String master) {
		String url = request.getRequestURI();
		if (hasDebug(url)) {
			String caller = request.getParameter("caller");
			if (caller != null)
				url += "?caller=" + caller;
			url = url.replaceAll("'", "''");
			DebugLog log = new DebugLog(url, employee.getEm_name(), master);
			request.setAttribute(debugAttribute, log.getDl_id());
			logs.put(log.getDl_id(), log);
			threads.put(log.getDl_id(), Thread.currentThread());
			Thread.yield();
		}
	}

	/**
	 * 请求执行成功
	 * 
	 * @param logId
	 *            debugr日志ID
	 */
	public void success(HttpServletRequest request, String logId) {
		DebugLog log = getLogById(logId);
		if (log != null) {
			log.setDl_step(99);
			log.setDl_time(System.currentTimeMillis() - log.getDl_time());
			super.log(log);
		}
		logs.remove(logId);

		Thread thread = threads.get(logId);
		if (thread != null)
			threads.remove(thread);
	}

	/**
	 * 请求执行失败
	 * 
	 * @param logId
	 *            debugr日志ID
	 */
	public void failure(HttpServletRequest request, String logId) {
		DebugLog log = getLogById(logId);
		if (log != null) {
			log.setDl_step(-1);
			log.setDl_time(System.currentTimeMillis() - log.getDl_time());
			super.log(log);
		}
		logs.remove(logId);

		Thread thread = threads.get(logId);
		if (thread != null)
			threads.remove(thread);
	}

	/**
	 * 请求执行超时
	 * 
	 * @param logId
	 *            debugr日志ID
	 */
	public void timeout(HttpServletRequest request, String logId) {
		DebugLog log = getLogById(logId);
		if (log != null) {
			log.setDl_step(99);
			log.setDl_time(System.currentTimeMillis() - log.getDl_time());
			super.log(log);
		}
		logs.remove(logId);
	}

	private DebugLog getLogById(String logId) {
		return logs.get(logId);
	}

	private boolean hasDebug(String url) {
		return !"/ERP/oa/info/getPagingRelease.action".equals(url) && !"/ERP/common/lazyTree.action".equals(url) && !"/ERP/".equals(url)
				&& !"/ERP/common/logout.action".equals(url);
	}

	@Override
	public void switchOver() {
		super.switchOver();
		// 轮询处理日志文件的时候，同时检查内存里面是否有超时的请求
		try {
			checkTimeoutRequest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkTimeoutRequest() {
		Iterator<String> idIterator = logs.keySet().iterator();
		String logId = null;
		while (idIterator.hasNext()) {
			logId = idIterator.next();
			DebugLog log = logs.get(logId);
			if (log != null && log.isTimeout()) {
				Thread thread = threads.get(logId);
				Thread tmp = thread;
				thread = null;
				if (tmp != null)
					tmp.interrupt();
				threads.remove(logId);
			}
		}
	}

}
