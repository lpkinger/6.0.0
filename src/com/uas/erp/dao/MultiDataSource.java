package com.uas.erp.dao;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

public class MultiDataSource extends DruidDataSource implements DataSource, ApplicationContextAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext applicationContext = null;
	private DataSource dataSource = null;
	private Properties connectionProperties = null;

	public DruidPooledConnection getConnection() throws SQLException {
		DruidDataSource dataSource = (DruidDataSource) getDataSource();
		return dataSource.getConnection();
	}

	public Connection getConnection(String arg0, String arg1) throws SQLException {
		return getDataSource().getConnection(arg0, arg1);
	}

	public Properties getConnectionProperties() {
		return connectionProperties;
	}

	public void setConnectionProperties(Properties connectionProperties) {
		this.connectionProperties = connectionProperties;
	}

	public void setLogWriter(PrintWriter arg0) throws SQLException {
		getDataSource().setLogWriter(arg0);
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public DataSource getDataSource(String dataSourceName) {
		if (dataSourceName == null || dataSourceName.equals("")) {
			return this.dataSource;
		}
		return (DataSource) this.applicationContext.getBean(dataSourceName);
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return getDataSource(SpObserver.getSp());
	}
}