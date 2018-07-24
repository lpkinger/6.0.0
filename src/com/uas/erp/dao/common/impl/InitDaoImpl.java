package com.uas.erp.dao.common.impl;

import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.PooledConnection;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.InitDao;
import com.uas.erp.model.InitData;
import com.uas.erp.model.InitDetail;
import com.uas.erp.model.InitLog;
import com.uas.erp.model.InitNodes;
import com.uas.erp.model.InitToFormal;
import com.uas.erp.model.Initialize;

@Repository
public class InitDaoImpl extends BaseDao implements InitDao {

	@Override
	public List<Initialize> getInitializes(int pid) {
		try {
			List<Initialize> sns = getJdbcTemplate().query("select * from initialize where in_pid=? order by in_detno",
					new BeanPropertyRowMapper<Initialize>(Initialize.class), pid);
			return sns;
		} catch (EmptyResultDataAccessException exception) {
			return null;
		}
	}

	@Override
	public List<InitDetail> getInitDetails(String caller) {
		try {
			List<InitDetail> sns = getJdbcTemplate().query("select * from initdetail where id_caller=? order by id_detno",
					new BeanPropertyRowMapper<InitDetail>(InitDetail.class), caller);
			return sns;
		} catch (EmptyResultDataAccessException exception) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<InitData> getInitDatas(String condition) {
		try {
			List<InitData> sns = getJdbcTemplate().query("select * from initdata where " + condition + " ORDER BY id_detno",
					new BeanPropertyRowMapper<InitData>(InitData.class));
			return sns;
		} catch (EmptyResultDataAccessException exception) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 调用存储过程保存大量导入的数据
	 */
	@Transactional(propagation = Propagation.REQUIRED, timeout = 30)
	public void save(final List<InitData> datas) {
		getJdbcTemplate().execute(new CallableStatementCreator() {
			@Override
			public CallableStatement createCallableStatement(Connection conn) throws SQLException {
				CallableStatement cs = conn.prepareCall("{call INIT_SAVEDATA(?)}");
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
	 * 调用存储过程插入正式数据
	 */
	@Transactional(propagation = Propagation.REQUIRED, timeout = 60)
	public String toFormalData(final String keyField, final String tableName, final List<InitToFormal> datas) {
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
	public List<InitLog> getInitHistory(String caller) {
		try {
			List<InitLog> sns = getJdbcTemplate()
					.query("select il_id,il_caller,il_sequence,il_date,il_checked,il_success,il_toformal,il_count,il_man from initlog where il_caller=? order by il_sequence",
							new BeanPropertyRowMapper<InitLog>(InitLog.class), caller);
			return sns;
		} catch (EmptyResultDataAccessException exception) {
			return null;
		}
	}

	@Override
	public List<InitDetail> getInitDetailsByCondition(String caller, String condition) {
		try {
			List<InitDetail> sns = getJdbcTemplate().query(
					"select * from initdetail where id_caller=? AND (" + condition + ") order by id_detno",
					new BeanPropertyRowMapper<InitDetail>(InitDetail.class), caller);
			return sns;
		} catch (EmptyResultDataAccessException exception) {
			return null;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, timeout = 10)
	public InputStream getResult(final int il_id) {
		try {
			return getJdbcTemplate().execute(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection conn) throws SQLException {
					CallableStatement cs = conn.prepareCall("{call C_GETFIELDDATA (?,?,?,?)}");
					cs.setString(1, "InitLog");
					cs.setString(2, "il_result");
					cs.setString(3, "il_id=" + il_id);
					cs.registerOutParameter(4, java.sql.Types.CLOB);
					return cs;
				}
			}, new CallableStatementCallback<InputStream>() {
				@Override
				public InputStream doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
					cs.execute();
					return cs.getClob(4).getAsciiStream();
				}

			});
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
		}
		return null;
	}

	@Transactional(propagation = Propagation.REQUIRED, timeout = 30)
	public synchronized void saveErrorMsg(final int id, final String errors) {
		try {
			String sql = "declare v_result clob;v_err clob := :1;v_id number := :2;begin select il_result into v_result from InitLog where il_id=v_id for update;if nvl(dbms_lob.getlength(v_result),0) = 0 then v_result := ' '; end if;dbms_lob.append(v_result,v_err);update InitLog set il_result=v_result where il_id=v_id;end;";
			getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
				@Override
				protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException, DataAccessException {
					lob.setClobAsString(ps, 1, errors);
					ps.setInt(2, id);
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, timeout = 30)
	public void logNodes(final List<InitNodes> nodes) {
		getJdbcTemplate()
				.batchUpdate(
						"INSERT /*+Append*/ INTO InitNodes (in_table, in_ilid, in_logic, in_field, in_value, in_idid, in_type) values (?,?,?,?,?,?,?)",
						new BatchPreparedStatementSetter() {

							@Override
							public void setValues(PreparedStatement ps, int i) throws SQLException {
								InitNodes node = nodes.get(i);
								ps.setString(1, node.getId_table());
								ps.setObject(2, node.getId_ilid());
								ps.setString(3, node.getId_logic());
								ps.setString(4, node.getId_field());
								ps.setString(5, node.getId_value());
								ps.setObject(6, node.getId_id());
								ps.setObject(7, node.getId_type());

							}

							@Override
							public int getBatchSize() {
								return nodes.size();
							}
						});
	}
}
