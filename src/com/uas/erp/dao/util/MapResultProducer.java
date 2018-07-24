package com.uas.erp.dao.util;

import java.util.Map;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class MapResultProducer extends ResultProducer<Map<String, Object>> {

	public MapResultProducer(JdbcTemplate jdbcTemplate, String dataSource, String executable, ResultQueue<Map<String, Object>> resultQueue) {
		super(jdbcTemplate, dataSource, executable, resultQueue, new ColumnMapRowMapper());
	}

}
