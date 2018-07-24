package com.uas.erp.dao.util;

import java.util.Map;

/**
 * 消费类型为{@code Map<String, Object>}
 * 
 * @author yingp
 * 
 */
public class MapResultConsumer extends ResultConsumer<Map<String, Object>> {

	public MapResultConsumer(ResultQueue<Map<String, Object>> resultQueue, MapResultProcesser processer) {
		super(resultQueue, processer);
	}

}
