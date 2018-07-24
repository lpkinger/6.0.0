package com.uas.erp.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class SqlServerDao {
	private volatile static SqlServerDao myDao;

	private SqlServerDao() {

	}

	public static SqlServerDao getSqlServerDao() {
		if (myDao == null) {
			synchronized (SqlServerDao.class) {
				if (myDao == null) {
					myDao = new SqlServerDao();
				}
			}
		}
		return myDao;
	}

	private SqlServerDataSource dataSource = SqlServerDataSource
			.getDataSource();

	public boolean execute(String sql) {
		Connection connection = null;
		Statement statement = null;
		boolean bool = false;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			bool = statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return bool;
	}

	public boolean execute(String... sqls) {
		Connection connection = null;
		Statement statement = null;
		boolean bool = false;
		try {
			if (sqls.length > 0) {
				StringBuffer sb = new StringBuffer("begin ");
				for (String sql : sqls) {
					sb.append("execute immediate '")
							.append(sql.replace("'", "''")).append("';");
				}
				sb.append("end;");
				connection = dataSource.getConnection();
				statement = connection.createStatement();
				bool = statement.execute(sb.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return bool;
	}

	public SqlRowList query(String sql) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		SqlRowList sqlMap = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			ResultSetMetaData metaData = resultSet.getMetaData();
			int numcols = metaData.getColumnCount();
			sqlMap = new SqlRowList();
			Map<String, Object> map = null;
			while (resultSet.next()) {
				map = new HashMap<String, Object>();
				for (int i = 1; i <= numcols; i++) {
					map.put(metaData.getColumnName(i).toUpperCase(),
							resultSet.getObject(i));
				}
				sqlMap.getResultList().add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return sqlMap;
	}

	/**
	 * 一个字段，一条结果
	 * 
	 * @param sql
	 * @return requiredType 返回值类型
	 */
	public <T> T query(String sql, Class<T> requiredType) {
		SqlRowList srs = query(sql);
		if (srs.next()) {
			RowConvert<T> convert = new RowConvert<T>(requiredType);
			return convert.convert(srs.getObject(1));
		} else {
			return null;
		}
	}

	public void execProcedure(String name, Object... args) {
		Connection connection = null;
		CallableStatement statement = null;
		try {
			StringBuffer sql = new StringBuffer("call ").append(name).append(
					"(");
			for (int i = 0, len = args.length; i < len; i++) {
				if (i > 0) {
					sql.append(",");
				}
				sql.append("?");
			}
			connection = dataSource.getConnection();
			statement = connection.prepareCall(sql.toString());
			for (int i = 0, len = args.length; i < len; i++) {
				statement.setObject(i + 1, args[i]);
			}
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public String callProcedure(String name, Object... args) {
		Connection connection = null;
		CallableStatement statement = null;
		String string = null;
		try {
			StringBuffer sql = new StringBuffer("call ").append(name).append(
					"(");
			for (int i = 0, len = args.length; i < len; i++) {
				sql.append("?,");
			}
			sql.append("?");
			connection = dataSource.getConnection();
			statement = connection.prepareCall(sql.toString());
			for (int i = 0, len = args.length; i < len; i++) {
				statement.setObject(i + 1, args[i]);
			}
			statement.registerOutParameter(args.length + 1,
					java.sql.Types.VARCHAR);
			statement.executeUpdate();
			string = statement.getString(args.length + 1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return string;
	}
}
