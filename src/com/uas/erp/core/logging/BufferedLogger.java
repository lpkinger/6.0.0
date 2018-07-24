package com.uas.erp.core.logging;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 支持异步日志记录的工具类<br>
 * 写日志时，会尝试将日志消息快速写到本地文件，失败的情况下直接写入到持久层
 * 
 * @author yingp
 *
 */
public abstract class BufferedLogger<T extends BufferedLogable> {

	/**
	 * 日志文件夹路径
	 */
	private String logPath;

	/**
	 * 日志文件名
	 */
	private String currentLogFile;

	private Class<T> logClass;

	/**
	 * 日志文件工具
	 */
	private FileBuffer fileBuffer;

	/**
	 * 日志持久化service
	 */
	private LogService<T> logService;

	public BufferedLogger() {
	}

	/**
	 * @param basePath
	 *            日志文件根路径
	 * @param logFile
	 *            日志文件名
	 * @param logService
	 *            持久化工具
	 */
	public BufferedLogger(String basePath, Class<T> logClass, LogService<T> logService) {
		this.logPath = basePath + File.separator + "buff_log" + File.separator + logClass.getName();
		this.logClass = logClass;
		this.fileBuffer = new FileBuffer(logPath, getFileName());
		this.logService = logService;
		// 检查是否有遗留日志未持久化的
		leaveOver();
	}

	/**
	 * 写日志操作
	 * 
	 * @param bufferedLogable
	 */
	public void log(T bufferedLogable) {
		// 先尝试写到日志文件
		if (!fileBuffer.append(bufferedLogable.bufferedMessage()) && logService != null)
			// 写入失败的情况下，直接持久化
			logService.save(bufferedLogable);
	}

	/**
	 * 读日志
	 * 
	 * @param cls
	 * @return
	 */
	protected Set<T> read(FileBuffer buffer) {
		Set<T> logs = new HashSet<T>();
		String bufferedMessage = null;
		while ((bufferedMessage = buffer.readLine()) != null) {
			try {
				T instance = logClass.newInstance();
				instance.bufferedLog(bufferedMessage);
				logs.add(instance);
			} catch (InstantiationException e) {

			} catch (IllegalAccessException e) {

			}
		}
		return logs;
	}

	/**
	 * 日志文件名
	 * 
	 * @return
	 */
	protected String getFileName() {
		this.currentLogFile = String.valueOf(System.currentTimeMillis());
		return this.currentLogFile;
	}

	/**
	 * 切换日志文件
	 * 
	 * @param newLogFile
	 */
	public void switchOver() {
		FileBuffer oldFileBuffer = this.fileBuffer;
		if (!oldFileBuffer.isEmpty()) {
			// 创建新的日志文件，不影响日志的写入
			this.fileBuffer = new FileBuffer(logPath, getFileName());
			// 旧日志文件的内容取出，并持久化
			if (logService != null)
				logService.save(read(oldFileBuffer));
			// 删除旧日志文件
			oldFileBuffer.delete();
		}
	}

	/**
	 * 持久化遗留日志文件
	 */
	protected void leaveOver() {
		if (logService != null) {
			File folder = new File(logPath);
			if (folder.isDirectory()) {
				File[] files = folder.listFiles();
				if (files != null) {
					String fileName = null;
					for (File file : files) {
						fileName = file.getName();
						if (!fileName.equals(currentLogFile)) {
							FileBuffer buffer = new FileBuffer(logPath, fileName);
							try {
								logService.save(read(buffer));
							} catch (Exception e) {

							} finally {
								buffer.delete();
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (this.fileBuffer != null)
			this.fileBuffer.close();
	}

}
