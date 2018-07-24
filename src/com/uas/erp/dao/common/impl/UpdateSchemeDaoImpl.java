package com.uas.erp.dao.common.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.PooledConnection;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.UpdateSchemeDao;
import com.uas.erp.model.InitToFormal;
import com.uas.erp.model.TableColumnProperty;
import com.uas.erp.model.UpdateScheme;
import com.uas.erp.model.UpdateSchemeData;
import com.uas.erp.model.UpdateSchemeDetail;

@Repository
public class UpdateSchemeDaoImpl extends BaseDao implements UpdateSchemeDao {

	/**
	 * 调用存储过程保存大量导入的数据
	 */
	@Transactional(propagation = Propagation.REQUIRED, timeout = 30)
	public void save(final List<UpdateSchemeData> datas) {
		getJdbcTemplate().execute(new CallableStatementCreator() {
			@Override
			public CallableStatement createCallableStatement(Connection conn) throws SQLException {
				CallableStatement cs = conn.prepareCall("{call SP_SAVEUPDATEDATA(?)}");
				Connection ct = getNativeConnection(conn);
				ArrayDescriptor tabDesc = ArrayDescriptor.createDescriptor("UAS_ORA_INITDATA_LIST_TAB", ct);
				ARRAY vArray = new ARRAY(tabDesc, ct, datas.toArray());
				cs.setArray(1, vArray);
				return cs;
			}
		}, new CallableStatementCallback<Void>() {
			@Override
			public Void doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				cs.execute();
				return null;
			}
		});
	}

	/**
	 * 调用初始化导入的存储过程更新数据
	 */
	@Transactional(propagation = Propagation.REQUIRED, timeout = 60)
	public String updateData(final String keyField, final String tableName, final List<InitToFormal> datas) {
		try {
			return getJdbcTemplate().execute(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection conn) throws SQLException {
					CallableStatement cs = conn.prepareCall("{call INIT_TOFORMALDATA(?,?,?,?)}");
					Connection ct = getNativeConnection(conn);
					ArrayDescriptor tabDesc = ArrayDescriptor.createDescriptor("UAS_ORA_INITTOFORMAL_LIST_TAB", ct);
					ARRAY vArray = new ARRAY(tabDesc, ct, datas.toArray());
					cs.setString(1, keyField);
					cs.setString(2, tableName);
					cs.setArray(3, vArray);
					cs.registerOutParameter(4, java.sql.Types.VARCHAR);
					return cs;
				}
			}, new CallableStatementCallback<String>() {
				@Override
				public String doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
					cs.execute();
					return cs.getString(4);
				}
			});
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("resource")
	public static Connection getNativeConnection(Connection con) throws SQLException {
		Connection conToUse = con;
		if (conToUse instanceof PooledConnection) {
			conToUse = ((PooledConnection) con).getConnection();
		}
		while (conToUse instanceof ConnectionProxy) {
			conToUse = ((ConnectionProxy) conToUse).getRawObject();
		}
		return conToUse;
	}

	@Override
	public UpdateScheme getUpdateScheme(String id) {
		UpdateScheme scheme = null;
		try {
			scheme = getJdbcTemplate().queryForObject("select * from UpdateScheme where id_=?",
					new BeanPropertyRowMapper<UpdateScheme>(UpdateScheme.class), id);
			scheme.setDetails(query("select * from updateschemedetail where scheme_id_=?", UpdateSchemeDetail.class, id));
			// 同时考虑TAB_COL_PROPERTY
			scheme.setProperties(query("select * from TAB_COL_PROPERTY where tablename_=?", TableColumnProperty.class, scheme.getTable_()));
		} catch (EmptyResultDataAccessException e) {

		}
		return scheme;
	}

	@Override
	public void saveUpdateScheme(UpdateScheme scheme) {
		save(scheme, "UpdateScheme");
		save(scheme.getDetails(), "updateschemedetail");
		// 同时考虑TAB_COL_PROPERTY
		save(scheme.getProperties(), "TAB_COL_PROPERTY");
	}
}
