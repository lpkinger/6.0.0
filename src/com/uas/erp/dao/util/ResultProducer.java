package com.uas.erp.dao.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.uas.erp.dao.SpObserver;

/**
 * 结果生产方
 * 
 * @author yingp
 *
 * @param <T>
 */
public class ResultProducer<T> implements Runnable {

	private final ResultQueue<T> resultQueue;

	private final JdbcTemplate jdbcTemplate;

	private final String executable;

	private final String dataSource;

	private final RowMapper<T> rowMapper;

	/**
	 * @param jdbcTemplate
	 * @param 数据源名
	 *            （账套名）
	 * @param executable
	 * @param resultQueue
	 * @param rowMapper
	 *            解析ResultSet
	 */
	public ResultProducer(JdbcTemplate jdbcTemplate, String dataSource, String executable, ResultQueue<T> resultQueue,
			RowMapper<T> rowMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.dataSource = dataSource;
		this.executable = executable;
		this.resultQueue = resultQueue;
		this.rowMapper = rowMapper;
	}

	@Override
	public void run() {
		SpObserver.putSp(dataSource);
		jdbcTemplate.query(executable, new ResultSetExtractor<Void>() {

			@Override
			public Void extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next() && !resultQueue.isClose()) {
					T result;
					try {
						result = rowMapper.mapRow(rs, rs.getRow());
						resultQueue.getQueue().put(result);
					} catch (Exception e) {
						e.printStackTrace();
						// 出错情况下示意中断
						resultQueue.close();
						// 剩余结果也不再解析
						break;
					}
				}
				return null;
			}

		});
		resultQueue.close();
	}

	public void stop() {
		resultQueue.close();
	}

}
