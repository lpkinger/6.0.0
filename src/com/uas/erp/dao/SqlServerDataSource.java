package com.uas.erp.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;

public class SqlServerDataSource {

	private volatile static SqlServerDataSource dataSource;

	private SqlServerDataSource() {

	}

	public static SqlServerDataSource getDataSource() {
		if (dataSource == null) {
			synchronized (SqlServerDataSource.class) {
				if (dataSource == null) {
					dataSource = new SqlServerDataSource();
				}
			}
		}
		return dataSource;
	}

	public Connection getConnection() throws SQLException {
		javax.sql.DataSource dataSource = SqlServerDataSource.setupDataSource();
		if (dataSource != null) {
			return dataSource.getConnection();
		}
		return null;
	}

	/**
	 * 创建数据源
	 * 
	 * @return
	 */
	public static javax.sql.DataSource setupDataSource() {
		DruidDataSource ds = new DruidDataSource();
		ds.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		ds.setUsername("sa");
		ds.setPassword("05140755");
		ds.setUrl("jdbc:sqlserver://183.57.42.158:1433; DatabaseName=fivestarmf");
		return ds;
	}

	/**
	 * 关闭数据源
	 * 
	 * @param ds
	 * @throws SQLException
	 */
	public static void shutdownDataSource(javax.sql.DataSource ds) throws SQLException {
		DruidDataSource bds = (DruidDataSource) ds;
		bds.close();
	}
}
