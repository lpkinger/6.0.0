package com.uas.erp.service.common.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.uas.erp.core.logging.BufferedLoggerManager;

/**
 * 定时任务<br>
 * 将debug日志文件的内容每隔30秒持久化一次
 * 
 * @author yingp
 *
 */
@Component
@EnableAsync
@EnableScheduling
public class DebugLogTask {

	private final static DebugBufferedLogger logger = BufferedLoggerManager.getLogger(DebugBufferedLogger.class);

	@Scheduled(fixedRate = 30 * 1000)
	@Async
	public void execute() {
		logger.switchOver();
	}

}
