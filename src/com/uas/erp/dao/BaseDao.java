package com.uas.erp.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.bind.Status;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.common.SettingDao;
import com.uas.erp.model.DBFindSet;
import com.uas.erp.model.DBFindSetDetail;
import com.uas.erp.model.DBFindSetUI;
import com.uas.erp.model.DataDictionaryDetail;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.Master;
import com.uas.erp.model.MessageLog;

/**
 * @since 2013-1-9
 *        <p>
 *        用到SqlRowSet的方法全部用List<Map<String, Object>>代替，
 *        以解决因jdk问题而导致jboss对RowSet的不支持，调取结果集时出现NullPointerException
 *        </p>
 * 
 * 
 * @author yingp
 * 
 * @since 2015-11-26
 *        <p>
 *        去掉支持Oracle9i的OracleLobHandler,改为使用{@link DefaultLobHandler}
 *        ,在db-config.xml注入
 *        </p>
 */
@Repository("baseDao")
public class BaseDao extends JdbcDaoSupport {

	/**
	 * 日志记录工具
	 */
	public final Logger<BaseDao> logger = new Logger<BaseDao>(this);

	@Autowired
	protected LobHandler lobHandler;

	/**
	 * 参数配置中心
	 */
	@Autowired
	public ConfigFactory configFactory;

	/**
	 * 断言工具
	 */
	public final AssertRepository<BaseDao> asserts = new AssertRepository<BaseDao>(this);

	static final String SELECT_TABLES = "SELECT count(1) FROM user_tables WHERE table_name=?";// 从数据库查找表
	static final String SELECT_VIEWS = "SELECT count(1) FROM user_views WHERE view_name=?";// 从数据库查找视图
	static final String SELECT_DDD = "select  a.table_name,a.column_name,a.data_type,a.data_length,comments from  User_Tab_Columns a "
			+ "left join user_col_comments  b on  a.table_name=b.table_name and a.column_name=b.column_name  where a.table_name=? order by column_id";// 从数据字典查找表
	static final String CREATE_SEQ = "CREATE SEQUENCE ?" + // 创建序列
			" MINVALUE 1 MAXVALUE 99999999999 INCREMENT BY 1 START WITH 3000 CACHE 20 NOORDER NOCYCLE ";
	static final String FORM_TABLE = "SELECT fo_table FROM form WHERE fo_caller=?";
	static final String DETAILGRID_TABLE = "SELECT dg_table FROM detailgrid WHERE dg_caller=?";

	/**
	 * @param caller
	 *            单据Caller
	 * @param mid
	 *            单据id
	 * @param groupid
	 *            可阅读者的id字符串
	 */
	public boolean setReader(String caller, int mid, String groupid) {
		String res = callProcedure("setreader", new Object[] { caller, mid, groupid });
		return res.equals("true");
	}

	/**
	 * add object (objName = tableName)
	 * 
	 * @param objForSave
	 *            ,extends Saveable
	 * @return keyValue
	 */
	public Number saveAndReturnKey(Saveable objForSave) {
		return saveAndReturnKey(objForSave, objForSave.table(), objForSave.keyColumns());
	}

	/**
	 * add object
	 * 
	 * @param objForSave
	 * @param tableName
	 * @param columnAndValue
	 * @param keyColumns
	 * @return keyValue
	 */
	public Number saveAndReturnKey(Object objForSave, String tableName, String... keyColumns) {
		SimpleJdbcInsert insertActor = getSimpleJdbcInsert();
		insertActor.setTableName(tableName);
		insertActor.usingGeneratedKeyColumns(keyColumns);
		Number newId = insertActor.executeAndReturnKey(new BeanPropertySqlParameterSource(objForSave));
		return newId;
	}

	/**
	 * @param objForSave
	 * @return
	 */

	public void save(Saveable objForSave) {
		SimpleJdbcInsert insertActor = getSimpleJdbcInsert();
		insertActor.setTableName(objForSave.table());
		insertActor.execute(new BeanPropertySqlParameterSource(objForSave));
	}

	/**
	 * 批量保存
	 * 
	 * @param objsForSave
	 * @return
	 */

	public void save(Collection<? extends Saveable> objsForSave) {
		SqlParameterSource[] sqlSource = new SqlParameterSource[objsForSave.size()];
		int i = 0;
		String tableName = null;
		for (Saveable obj : objsForSave) {
			sqlSource[i++] = new BeanPropertySqlParameterSource(obj);
			if (tableName == null)
				tableName = obj.table();
		}
		SimpleJdbcInsert insertActor = getSimpleJdbcInsert();
		insertActor.setTableName(tableName);
		insertActor.executeBatch(sqlSource);
	}

	/**
	 * @param objForSave
	 * @param tableName
	 * @return
	 */
	public void save(Object objForSave, String tableName) {
		SimpleJdbcInsert insertActor = getSimpleJdbcInsert();
		insertActor.setTableName(tableName);
		insertActor.execute(new BeanPropertySqlParameterSource(objForSave));
	}

	/**
	 * 批量保存对象
	 * 
	 * @param objsForSave
	 * @param tableName
	 * @return
	 */
	public <T> void save(Collection<T> objsForSave, String tableName) {
		SqlParameterSource[] sqlSource = new SqlParameterSource[objsForSave.size()];
		int i = 0;
		for (T obj : objsForSave) {
			sqlSource[i++] = new BeanPropertySqlParameterSource(obj);
		}
		SimpleJdbcInsert insertActor = getSimpleJdbcInsert();
		insertActor.setTableName(tableName);
		insertActor.executeBatch(sqlSource);
	}

	/**
	 * 
	 * @param objForSave
	 * @param tableName
	 * @param keyColumns
	 * @return
	 */
	public void save(Object objForSave, String tableName, String... keyColumns) {
		SimpleJdbcInsert insertActor = getSimpleJdbcInsert();
		insertActor.setTableName(tableName);
		insertActor.execute(new BeanPropertySqlParameterSource(objForSave));
	}

	/**
	 * 
	 * @param objForSave
	 * @param tableName
	 * @param columnAndValue
	 * @param keyColumns
	 * @return
	 */
	public Number saveAndReturnKey(String tableName, Map<String, Object> columnAndValue, String... keyColumns) {
		SimpleJdbcInsert insertActor = getSimpleJdbcInsert();
		insertActor.setTableName(tableName);
		insertActor.usingGeneratedKeyColumns(keyColumns);
		Number newId = insertActor.executeAndReturnKey(columnAndValue);
		return newId;
	}

	public SimpleJdbcInsert getSimpleJdbcInsert() {
		return new SimpleJdbcInsert(getJdbcTemplate());
	}

	public SimpleJdbcInsert getSimpleJdbcInsert(String tableName) {
		return new SimpleJdbcInsert(getJdbcTemplate());
	}

	/**
	 * print sql
	 */
	public void traceSql(String sql) {
		System.out.println("JDBC:" + sql);
	}

	public List<Map<String, Object>> queryForList(String sql) {
		try {
			return this.getJdbcTemplate().queryForList(sql);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public <T> List<T> queryForList(String sql, Class<T> elementType) {
		try {
			return this.getJdbcTemplate().queryForList(sql, elementType);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Map<String, Object>> queryForList(String sql, Object... args) {
		try {
			return this.getJdbcTemplate().queryForList(sql, args);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public <T> List<T> queryForList(String sql, Object[] args, Class<T> elementType) {
		try {
			return this.getJdbcTemplate().queryForList(sql, args, elementType);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) {
		try {
			return this.getJdbcTemplate().queryForList(sql, elementType, args);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public <T> List<T> query(String sql, Object[] args, Class<T> elementType) {
		try {
			return this.getJdbcTemplate().query(sql, args, new BeanPropertyRowMapper<T>(elementType));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public <T> List<T> query(String sql, Class<T> elementType) {
		try {
			return this.getJdbcTemplate().query(sql, new BeanPropertyRowMapper<T>(elementType));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public <T> List<T> query(String sql, Class<T> elementType, Object... args) {
		try {
			return this.getJdbcTemplate().query(sql, new BeanPropertyRowMapper<T>(elementType), args);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public <T> T queryForObject(String sql, Class<T> elementType, Object... args) {
		try {
			return this.getJdbcTemplate().queryForObject(sql, elementType, args);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public <T> T queryBean(String sql, Class<T> elementType, Object... args) {
		try {
			return this.getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<T>(elementType), args);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public void execute(String sql) {
		getJdbcTemplate().execute(sql);
	}

	public synchronized boolean execute(String sql, Object... objs) {
		try {
			getJdbcTemplate().update(sql, objs);
			return true;
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
			return false;
		}
	}

	public void execute(List<String> sqls) {
		if (sqls.size() > 0) {
			StringBuffer sb = new StringBuffer("begin ");
			for (String sql : sqls) {
				sb.append("execute immediate '").append(sql.replace("'", "''")).append("';");
			}
			sb.append("end;");
			getJdbcTemplate().execute(sb.toString());
		}
	}

	/**
	 * 执行sql <b>支持lob数据的操作</b>
	 * 
	 * @param sql
	 */
	public void execute(SqlMap sql) {
		sql.execute(getJdbcTemplate(), lobHandler);
	}

	/**
	 * 批量执行Sql
	 * 
	 * @param sqls
	 * @param callbackSqls
	 */
	public void batchExecute(List<SqlMap> sqls, List<String> callbackSqls) {
		if (sqls.size() > 0) {
			StringBuffer sb = new StringBuffer("begin ");
			for (SqlMap sql : sqls) {
				sb.append("execute immediate '").append(sql.getSql(false).replace("'", "''")).append("';");
			}
			for (String sql : callbackSqls) {
				sb.append("execute immediate '").append(sql.replace("'", "''")).append("';");
			}
			sb.append("end;");
			getJdbcTemplate().execute(sb.toString());
		}
	}

	/**
	 * @param execSqls
	 *            先执行的sql
	 * @param updateSqls
	 *            校验通过之后的更新语句
	 * @param checkSqls
	 *            校验语句，出错会回滚并返回异常信息
	 * @return
	 */
	public String executeWithCheck(List<String> execSqls, List<String> updateSqls, String... checkSqls) {
		if (execSqls.size() > 0) {
			StringBuffer sb = new StringBuffer("declare v_r varchar2(200);begin ");
			for (String sql : execSqls) {
				sb.append("execute immediate '").append(sql.replace("'", "''")).append("';");
			}
			for (String checkSql : checkSqls) {
				sb.append("BEGIN execute immediate '")
						.append(checkSql.replace("'", "''"))
						.append("' into v_r;if nvl(v_r,' ')<>' ' then rollback;RAISE_APPLICATION_ERROR(-20001,'ERROR_BEGIN'||v_r||'ERROR_END'); end if;exception when no_data_found then v_r := null;END;");
			}
			if (updateSqls != null) {
				for (String sql : updateSqls) {
					sb.append("execute immediate '").append(sql.replace("'", "''")).append("';");
				}
			}
			sb.append("end;");
			try {
				getJdbcTemplate().execute(sb.toString());
			} catch (Exception e) {
				if (e.getCause() instanceof java.sql.SQLException) {
					String errMsg = e.getCause().getMessage();
					if (errMsg.contains("ERROR_BEGIN"))
						return errMsg.substring(errMsg.indexOf("ERROR_BEGIN") + 11, errMsg.lastIndexOf("ERROR_END"));
				}
				return e.getMessage();
			}
		}
		return null;
	}

	public void execute(String[] sqls) {
		if (sqls.length > 0) {
			StringBuffer sb = new StringBuffer("begin ");
			for (String sql : sqls) {
				sb.append("execute immediate '").append(sql.replace("'", "''")).append("';");
			}
			sb.append("end;");
			getJdbcTemplate().execute(sb.toString());
		}
	}

	public void deleteById(Saveable obj, long id) {
		deleteById(obj.table(), obj.keyColumns()[0], id);
	}

	public void deleteById(String tablename, String keyField, long id) {
		deleteByCondition(tablename, keyField + "=" + id);
	}

	public void deleteByCondition(String tablename, String condition, Object... params) {
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM ");
		sb.append(tablename);
		sb.append(" WHERE ");
		sb.append(condition);
		execute(sb.toString(), params);
	}

	public void createTable(String sql) {
		SqlUpdate su = new SqlUpdate(getDataSource(), sql);
		su.compile();
		su.update();
	}

	/**
	 * 取指定系统参数设置
	 * 
	 * @param code
	 */
	public String getDBSetting(String code) {
		return getDBSetting("sys", code);
	}

	/**
	 * 取指定参数配置
	 * 
	 * @param caller
	 * @param code
	 *            参数编号
	 */
	public String getDBSetting(String caller, String code) {
		return configFactory.get(SpObserver.getSp(), caller, code);
	}

	/**
	 * 取指定参数配置(多值情况)
	 * 
	 * @param caller
	 * @param code
	 *            参数编号
	 */
	public String[] getDBSettingArray(String caller, String code) {
		return configFactory.getArray(SpObserver.getSp(), caller, code);
	}

	/**
	 * 判断指定系统参数是否配置为“是”
	 * 
	 * @param code
	 *            参数编号
	 */
	public boolean isDBSetting(String code) {
		return isDBSetting("sys", code);
	}

	/**
	 * 判断指定参数是否配置为“是”
	 * 
	 * @param caller
	 * @param code
	 *            参数编号
	 */
	public boolean isDBSetting(String caller, String code) {
		return configFactory.is(SpObserver.getSp(), caller, code);
	}

	/**
	 * 复制数据库一条或多条数据
	 * 
	 * @param fTab
	 *            Form Table
	 * @param tTab
	 *            To Table
	 * @param condition
	 * @param diffence
	 *            有差异的字段及值(注意:值如果为String，需要用单引号，而如果是to_date,sysdate等数据库函数或变量，
	 *            则无需用引号)
	 */
	public void copyRecord(String fTab, String tTab, String condition, Map<String, Object> diffence) {
		StringBuffer sb = new StringBuffer("begin ");
		sb.append("for rs in (select * from ");
		sb.append(fTab);
		sb.append(" where ");
		sb.append(condition);
		sb.append(") ");
		sb.append("loop ");
		Set<String> keys = diffence.keySet();
		for (String f : keys) {
			sb.append(" rs.");
			sb.append(f);
			sb.append(":=");
			sb.append(diffence.get(f));
			sb.append(";");
		}
		sb.append(" insert into ");
		sb.append(tTab);
		sb.append(" values rs;");
		sb.append(" end loop; ");
		sb.append(" end;");
		getJdbcTemplate().update(sb.toString());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getObjectByColumn(String tableName, String columnName, Object columnValue, Class className) {
		String sql = "select * from " + tableName + " where " + columnName + "=?";
		return getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper(className), columnValue);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<?> getAll(String tableName, Class className) {
		String sql = "select * from " + tableName;
		return getJdbcTemplate().query(sql, new BeanPropertyRowMapper(className));
	}

	public String getTableByFormCaller(String caller) {
		String sql = FORM_TABLE.replace("?", "'" + caller + "'");
		SqlRowList rs = queryForRowSet(sql);
		if (rs.next()) {
			return rs.getString(1);
		}
		return null;
	}

	/**
	 * {getDataStringByForm()}的新方法 支持jboss
	 * 注意queryForList取出来的数据集，里面的key是查询的字段的大写形式，而非查询的字段
	 * 直接遍历map.keySet()取得的数据，无法对应上实际查询字段
	 */
	public Map<String, Object> getFormData(Form form, String condition) {
		List<Map<String, Object>> list = getJdbcTemplate(form.getFo_table()).queryForList(form.getSql(condition));
		Iterator<Map<String, Object>> iter = list.iterator();
		Map<String, Object> map = null;
		if (iter.hasNext()) {
			map = iter.next();
			for (FormDetail detail : form.getFormDetails()) {
				String field = detail.getFd_field();
				if (field.contains(" ")) {// field有取别名
					String[] strs = field.split(" ");
					field = strs[strs.length - 1];
				}
				Object value = map.get(field.toUpperCase());
				value = value == null || value.equals("null") ? "" : value;
				if (value != null) {
					String classname = value.getClass().getSimpleName();
					if (classname.toUpperCase().equals("TIMESTAMP")) {
						Timestamp time = (Timestamp) value;
						try {
							value = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD_HMS);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				map.remove(field.toUpperCase());
				map.put(field, value);
			}
		}
		return map;
	}

	public String getDataStringByForm(Form form, String condition) {
		Map<String, Object> map = getFormData(form, condition);
		return map == null ? null : BaseUtil.parseMap2Str(map);
	}

	/**
	 * {getDataStringByDbfindSet()}的新方法 支持jboss
	 */
	public List<Map<String, Object>> getDbfindSetData(DBFindSet dbFindSet, String condition, String orderby, int page, int pageSize) {
		List<Map<String, Object>> list = getJdbcTemplate().queryForList(dbFindSet.getSql(condition, orderby, page, pageSize));
		Iterator<Map<String, Object>> iter = list.iterator();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		while (iter.hasNext()) {
			map = iter.next();
			for (DBFindSetDetail detail : dbFindSet.getDbFindSetDetails()) {
				String field = detail.getDd_fieldname();
				if (field.contains(" ")) {// column有取别名
					String[] strs = field.split(" ");
					field = strs[strs.length - 1];
				}
				Object value = map.get(field.toUpperCase());
				value = value == null || value.equals("null") ? "" : value;
				if (value != null) {
					String classname = value.getClass().getSimpleName();
					if (classname.toUpperCase().equals("TIMESTAMP")) {
						Timestamp time = (Timestamp) value;
						try {
							value = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD_HMS);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				map.remove(field.toUpperCase());
				map.put(field, value);
			}
			datas.add(map);
		}
		return datas;
	}

	public String getDataStringByDbfindSet(DBFindSet dbFindSet, String condition, String orderby, int page, int pageSize) {
		List<Map<String, Object>> maps = getDbfindSetData(dbFindSet, condition, orderby, page, pageSize);
		return BaseUtil.parseGridStore2Str(maps);
	}

	/**
	 * {getDataStringByDbfindSetUi()}的新方法 支持jboss
	 */
	public List<Map<String, Object>> getDbfindSetUiData(DBFindSetUI dbFindSetUI, String condition, int page, int pageSize) {
		List<Map<String, Object>> list = getJdbcTemplate().queryForList(dbFindSetUI.getSql(condition, page, pageSize));
		Iterator<Map<String, Object>> iter = list.iterator();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		String[] fields = dbFindSetUI.getDs_findtoui().split("#");
		Map<String, Object> map = null;
		String ff = null;
		while (iter.hasNext()) {
			map = iter.next();
			for (String field : fields) {
				field = field.split(",")[0];
				field = field.replace(".", "_");
				ff = field;
				if (ff.contains(" ")) {
					String[] strs = ff.split(" ");
					ff = strs[strs.length - 1];
				} else if (ff.contains("(")) {// sum(..),nvl(..)等带函数名的字段
					ff = ff.substring(ff.indexOf("(") + 1, ff.indexOf(")"));
				}
				Object value = map.get(ff.toUpperCase());
				value = value == null || value.equals("null") ? "" : value;
				if (value != null) {
					String classname = value.getClass().getSimpleName();
					if (classname.toUpperCase().equals("TIMESTAMP")) {
						Timestamp time = (Timestamp) value;
						try {
							value = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD_HMS);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				map.remove(field.toUpperCase());
				if (field.contains(" "))
					map.put(ff, value);
				else
					map.put(field, value);
			}
			datas.add(map);
		}
		return datas;
	}

	public String getDataStringByDbfindSetUi(DBFindSetUI dbFindSetUI, String condition, int page, int pageSize) {
		List<Map<String, Object>> maps = getDbfindSetUiData(dbFindSetUI, condition, page, pageSize);
		return BaseUtil.parseGridStore2Str(maps);
	}

	public int getCount(String sql) {
		SqlRowList rs = queryForRowSet(sql);
		if (rs.next()) {
			return rs.getInt(1);
		}
		return 0;
	}

	public int getCountByTable(String tablename) {
		SqlRowList rs = queryForRowSet("SELECT count(*) FROM " + tablename);
		if (rs.next()) {
			return rs.getInt(1);
		}
		return 0;
	}

	public Double getSummaryByField(String tablename, String summaryField, String condition) {
		SqlRowList rs = queryForRowSet("SELECT sum(nvl(" + summaryField + ",0)) FROM " + tablename + " WHERE " + condition);
		if (rs.next()) {
			return rs.getGeneralDouble(1, 6);
		}
		return 0.0;
	}

	public String getTableByDetailGridCaller(String caller) {
		String sql = DETAILGRID_TABLE.replace("?", "'" + caller + "'");
		SqlRowList rs = queryForRowSet(sql);
		if (rs.next()) {
			return rs.getString(1);
		}
		return null;
	}

	/**
	 * {getDataStringByDetailGrid()}的新方法 支持jboss
	 */
	public List<Map<String, Object>> getDetailGridData(List<DetailGrid> detailGrids, String condition, Employee employee, Integer start,
			Integer end) {
		String caller = detailGrids.get(0).getDg_caller();
		Object[] objs = getFieldsDataByCondition("Form", "fo_detailtable,fo_detailcondition,fo_detailgridorderby", "fo_caller='" + caller
				+ "'");
		Object table = detailGrids.get(0).getDg_table();
		if (objs != null) {// 优先用Form的配置
			if (objs[0] != null)
				table = objs[0];
			if (objs[1] != null) {
				if ("".equals(condition)) {
					condition = objs[1].toString();
				} else {
					int index = condition.toLowerCase().indexOf("order by");
					if (index > -1) {
						condition = condition.substring(0, index) + " AND " + objs[1] + " " + condition.substring(index);
					} else {
						condition += " AND " + objs[1];
					}
				}
			}
			if (objs[2] != null && objs[2].toString().toLowerCase().indexOf("order by") > -1) {
				int index = condition.toLowerCase().indexOf("order by");
				if (index > -1) {
					condition = condition.substring(0, index);
				}
				condition += " " + objs[2];
			}
		}
		String sql = SqlUtil.getQuerySqlByDetailGrid(detailGrids, String.valueOf(table), condition, employee, start, end);
		List<Map<String, Object>> list = getJdbcTemplate(detailGrids.get(0).getDg_table().split(" ")[0]).queryForList(sql);
		Iterator<Map<String, Object>> iter = list.iterator();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		while (iter.hasNext()) {
			map = iter.next();
			for (DetailGrid detail : detailGrids) {
				String field = detail.getDg_field();
				if (field.contains(" ")) {// column有取别名
					String[] strs = field.split(" ");
					field = strs[strs.length - 1];
				}
				Object value = map.get(field.toUpperCase());
				value = value == null || value.equals("null") ? "" : value;
				if (value != null) {
					String classname = value.getClass().getSimpleName();
					if (classname.toUpperCase().equals("TIMESTAMP")) {
						Timestamp time = (Timestamp) value;
						try {
							value = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD_HMS);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					String type = detail.getDg_type();
					if (type != null && type.matches("^checkcolumn-?\\d{1}$")) {
						// 数据库里是number类型，在Grid里面作为checkcolumn时，需将number数据按配置转化成true和false
						if (value == null || "".equals(value))
							value = false;
						else
							value = Integer.parseInt(value.toString()) == Integer.parseInt(type.substring(11).toString());
					}
				}
				map.remove(field.toUpperCase());
				map.put(field, value);
			}
			datas.add(map);
		}
		return datas;
	}

	public String getDataCount(List<DetailGrid> detailGrids, String condition) {
		StringBuffer fieldStr = new StringBuffer();
		String caller = detailGrids.get(0).getDg_caller();
		Object[] objs = getFieldsDataByCondition("Form", "fo_detailtable,fo_detailcondition,fo_detailgridorderby", "fo_caller='" + caller
				+ "'");
		Object table = detailGrids.get(0).getDg_table();
		if (objs != null) {// 优先用Form的配置
			if (StringUtil.hasText(objs[0]))
				table = objs[0];
			if (objs[1] != null) {
				if ("".equals(condition)) {
					condition = objs[1].toString();
				} else {
					int index = condition.toLowerCase().indexOf("order by");
					if (index > -1) {
						condition = condition.substring(0, index) + " AND " + objs[1] + " " + condition.substring(index);
					} else {
						condition += " AND " + objs[1];
					}
				}
			}
			if (objs[2] != null && objs[2].toString().toLowerCase().indexOf("order by") > -1) {
				int index = condition.toLowerCase().indexOf("order by");
				if (index > -1) {
					condition = condition.substring(0, index);
				}
				condition += " " + objs[2];
			}
		}
		String sql = "";
		for (DetailGrid detail : detailGrids) {
			if (detail.getDg_field().contains(" ")) {// 有别名的字段
				fieldStr.append(",").append(detail.getDg_field());
			}
			//sql = detail.getDg_table();
		}
		sql = "SELECT * FROM " + table;
		sql = "select count(1) from (SELECT tab.*" + fieldStr.toString() + " from (" + sql;
		sql += ") tab)";
		if (StringUtils.hasText(condition))
			sql += " WHERE " + condition;
		return sql;
	}

	public String getGridDataBySql(List<DetailGrid> detailGrids, String sql) {
		List<Map<String, Object>> list = getJdbcTemplate(detailGrids.get(0).getDg_table().split(" ")[0]).queryForList(sql);
		Iterator<Map<String, Object>> iter = list.iterator();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		while (iter.hasNext()) {
			map = iter.next();
			for (DetailGrid detail : detailGrids) {
				String field = detail.getDg_field();
				Object value = map.get(field.toUpperCase());
				value = value == null || value.equals("null") ? "" : value;
				if (value != null) {
					String classname = value.getClass().getSimpleName();
					if (classname.toUpperCase().equals("TIMESTAMP")) {
						Timestamp time = (Timestamp) value;
						try {
							value = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD_HMS);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					String type = detail.getDg_type();
					if (type != null && type.matches("^checkcolumn-?\\d{1}$")) {
						// 数据库里是number类型，在Grid里面作为checkcolumn时，需将number数据按配置转化成true和false
						value = Integer.parseInt(value.toString()) == Integer.parseInt(type.substring(11).toString());
					}
				}
				map.remove(field.toUpperCase());
				map.put(field, value);
			}
			datas.add(map);
		}
		return BaseUtil.parseGridStore2Str(datas);
	}

	public String getDataStringByDetailGrid(List<DetailGrid> detailGrids, String condition, Integer start, Integer end) {
		Employee employee = SystemSession.getUser();
		List<Map<String, Object>> maps = getDetailGridData(detailGrids, condition, employee, start, end);
		return BaseUtil.parseGridStore2Str(maps);
	}

	/**
	 * 获取序列号
	 * 
	 * @param seq
	 *            指定的序列名
	 */
	public int getSeqId(String seq) {
		try {
		/*	if ("true".equals(BaseUtil.getXmlSetting("group"))) {
				boolean isBase = checkIf("basedataset", "upper(bds_sequence)=upper('" + seq + "') and nvl(bds_editable,0)=1");
				if (isBase) {
					// 集团版基础资料(客户，供应商...)统一到资料中心取号
					String dataCenter = BaseUtil.getXmlSetting("dataSob");
					seq = dataCenter + "." + seq;
				}
			}*/
			String sql = "select " + seq + ".nextval from dual";
			SqlRowList rs = queryForRowSet(sql);
			if (rs.next()) {
				return rs.getInt(1);
			} else {// 如果不存在就创建序列
				int count = getCountByCondition("user_sequences", "Sequence_Name='" + seq.toUpperCase() + "'");
				if (count == 0)
					getJdbcTemplate().execute(CREATE_SEQ.replace("?", seq));
				return getSeqId(seq);
			}
		} catch (Exception e) {
			int count = getCountByCondition("user_sequences", "Sequence_Name='" + seq.toUpperCase() + "'");
			if (count == 0)
				getJdbcTemplate().execute(CREATE_SEQ.replace("?", seq));
			return getSeqId(seq);
		}
	}

	public void createTrigger(String tab, String keyField) {
		tab = tab.toUpperCase();
		int count = getCountByCondition("DBA_SEQUENCES", "sequence_name='" + tab + "_SEQ'");
		if (count == 0) {
			getJdbcTemplate().execute(CREATE_SEQ.replace("?", tab + "_SEQ"));
		}
		count = getCountByCondition("DBA_TRIGGERS", "trigger_name='" + tab + "_TRI'");
		if (count == 0) {
			StringBuffer sb = new StringBuffer();
			sb.append("create or replace TRIGGER ");
			sb.append(tab);
			sb.append("_TRI before insert on ");
			sb.append(tab);
			sb.append(" for each row begin select ");
			sb.append(tab);
			sb.append("_SEQ.nextval into:new.");
			sb.append(keyField);
			sb.append(" from dual; end;");
			execute(sb.toString());
		}
	}

	public void deleteTrigger(String tab) {
		tab = tab.toUpperCase();
		execute("DROP TRIGGER " + tab + "_TRI");
	}

	/**
	 * 需要从数据字典的配置更新table时，用到此方法
	 * 
	 * @param tablename
	 *            需要更新的tablename
	 */
	public JdbcTemplate getJdbcTemplate(String tablename) {
		/*
		 * if(!checkTableName(tablename)){ throw new RuntimeException("表：" +
		 * tablename + "未在数据字典定义!"); }
		 */
		return getJdbcTemplate();
	}

	/*
	 * public void checkTable(String tablename) { tablename =
	 * tablename.split("!")[0].split(" ")[0].split(",")[0].toUpperCase().trim();
	 * if (!checkTableName(tablename)) {// 说明该表不存在 List<DataDictionaryDetail>
	 * dataDictionaryDetails = getDataDictionaryDetails(tablename);// 拿到对应数据字典
	 * if (dataDictionaryDetails.size() > 0) { createTableByName(tablename,
	 * dataDictionaryDetails);// 创建表 updateDataList(tablename,
	 * dataDictionaryDetails);// 修改对应datalist配置 updateForm(tablename,
	 * dataDictionaryDetails);// 修改对应form配置 // ...修改其他一些配置... } else { throw new
	 * RuntimeException("表：" + tablename + "未在数据字典定义!"); } } }
	 */

	public boolean checkTableName(String tablename) {
		String sql = SELECT_TABLES.replace("?", "'" + tablename + "'");
		if (getCount(sql) == 0) {// 说明该表不存在
			sql = SELECT_VIEWS.replace("?", "'" + tablename + "'");
			if (getCount(sql) == 1) {
				return true;
			}
			return false;
		}
		return true;
	}

	/**
	 * 根据表名拿到数据字典
	 * 
	 * @param tablename
	 *            表名
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<DataDictionaryDetail> getDataDictionaryDetails(String tablename) {
		return getJdbcTemplate().query(SELECT_DDD, new BeanPropertyRowMapper(DataDictionaryDetail.class),
				tablename.split(" ")[0].toUpperCase());
	}

	/**
	 * 根据表名，从数据字典找到配置，并创建该表
	 * 
	 * @param tablename
	 *            表名
	 */
	/*
	 * public void createTableByName(String tablename,
	 * List<DataDictionaryDetail> dataDictionaryDetails) { StringBuffer sb = new
	 * StringBuffer("CREATE TABLE "); sb.append(tablename + " ("); for
	 * (DataDictionaryDetail ddd : dataDictionaryDetails) {
	 * sb.append(ddd.getCreateSql()); } String sql = sb.toString() +
	 * "CONSTRAINT " + tablename.toUpperCase() + "_PK PRIMARY KEY (" +
	 * dataDictionaryDetails.get(0).getDdd_primekey() + ")" + ")";
	 * createTable(sql); }
	 */

	/**
	 * 修改form及formdetail表配置
	 */
	public void updateForm(String tablename, List<DataDictionaryDetail> dataDictionaryDetails) {
	}

	/**
	 * 修改datalist及datalistdetail表配置
	 */
	public void updateDataList(String tablename, List<DataDictionaryDetail> dataDictionaryDetails) {
	}

	/**
	 * 一个字段，一条结果
	 * 
	 * @param tableName
	 *            对应要查询的表
	 * @param field
	 *            要查询的字段
	 * @param condition
	 *            查询条件
	 * @return field对应的数据
	 */
	public Object getFieldDataByCondition(String tableName, String field, String condition) {
		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append(field);
		sql.append(" FROM ");
		sql.append(tableName);
		sql.append(" WHERE ");
		sql.append(condition);
		SqlRowList srs = queryForRowSet(sql.toString());
		if (srs.next()) {
			return srs.getObject(1);
		} else {
			return null;
		}
	}

	/**
	 * 一个字段，一条结果
	 * 
	 * @param tableName
	 *            对应要查询的表
	 * @param field
	 *            要查询的字段
	 * @param condition
	 *            查询条件
	 * @return field对应的数据
	 */
	public <T> T getFieldValue(String tableName, String field, String condition, Class<T> requiredType) {
		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append(field);
		sql.append(" FROM ");
		sql.append(tableName);
		sql.append(" WHERE ");
		sql.append(condition);
		SqlRowList srs = queryForRowSet(sql.toString());
		if (srs.next()) {
			RowConvert<T> convert = new RowConvert<T>(requiredType);
			return convert.convert(srs.getObject(1));
		} else {
			return null;
		}
	}

	/**
	 * 一个字段，多条结果
	 * 
	 * @param tableName
	 *            对应要查询的表
	 * @param field
	 *            要查询的字段
	 * @param condition
	 *            查询条件
	 * @return field对应的数据
	 */
	public <T> List<T> getFieldValues(String tableName, String field, String condition, Class<T> requiredType) {
		StringBuffer sb = new StringBuffer("SELECT ");
		sb.append(field);
		sb.append(" FROM ");
		sb.append(tableName);
		sb.append(((condition == null || "".equals(condition)) ? "" : (" WHERE " + condition)));
		SqlRowList srs = queryForRowSet(sb.toString());
		List<T> list = new ArrayList<T>();
		RowConvert<T> convert = new RowConvert<T>(requiredType);
		while (srs.next()) {
			list.add(convert.convert(srs.getObject(1)));
		}
		return list;
	}

	/**
	 * 一个字段，多条结果
	 * 
	 * @param tableName
	 *            对应要查询的表
	 * @param field
	 *            要查询的字段
	 * @param condition
	 *            查询条件
	 * @return field对应的数据
	 */
	public List<Object> getFieldDatasByCondition(String tableName, String field, String condition) {
		StringBuffer sb = new StringBuffer("SELECT ");
		sb.append(field);
		sb.append(" FROM ");
		sb.append(tableName);
		sb.append(((condition == null || "".equals(condition)) ? "" : (" WHERE " + condition)));
		SqlRowList srs = queryForRowSet(sb.toString());
		List<Object> list = new ArrayList<Object>();
		while (srs.next()) {
			list.add(srs.getObject(1));
		}
		return list;
	}

	/**
	 * 多个字段，<=1条结果
	 * 
	 * @param tableName
	 *            对应要查询的表
	 * @param fields
	 *            要查询的字段集合
	 * @param condition
	 *            查询条件
	 * @return fields对应的数据
	 */
	public Object[] getFieldsDataByCondition(String tableName, String[] fields, String condition) {
		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append(BaseUtil.parseArray2Str(fields, ","));
		sql.append(" FROM ");
		sql.append(tableName);
		sql.append(" WHERE ");
		sql.append(condition);
		List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql.toString());
		Iterator<Map<String, Object>> iter = list.iterator();
		int length = fields.length;
		Object[] results = new Object[length];
		Object value = null;
		if (iter.hasNext()) {
			Map<String, Object> m = iter.next();
			for (int i = 0; i < length; i++) {
				String upperField = fields[i].toUpperCase();
				if (upperField.indexOf(" AS ") > 0) {
					upperField = upperField.split(" AS ")[1].trim();
				}
				value = m.get(upperField);
				if (value != null && value.getClass().getSimpleName().toUpperCase().equals("TIMESTAMP")) {
					Timestamp time = (Timestamp) value;
					try {
						value = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD_HMS);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				results[i] = value;
			}
			return results;
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public String[] getStringFieldsDataByCondition(String tableName, String[] fields, String condition) {
		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append(BaseUtil.parseArray2Str(fields, ","));
		sql.append(" FROM ");
		sql.append(tableName);
		sql.append(" WHERE ");
		sql.append(condition);
		List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql.toString());
		Iterator<Map<String, Object>> iter = list.iterator();
		int length = fields.length;
		String[] results = new String[length];
		String value = null;
		if (iter.hasNext()) {
			Map<String, Object> m = iter.next();
			for (int i = 0; i < length; i++) {
				value = String.valueOf(m.get(fields[i].toUpperCase()));
				if (value.equals(" ") || value == null) {
					value = null;
				}
				if (value != null && value.getClass().getSimpleName().toUpperCase().equals("TIMESTAMP")) {
					// Timestamp time = (Timestamp) value.;
					try {
						value = DateUtil.parseDateToString(new Date(value), Constant.YMD_HMS);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				results[i] = value;
			}
			return results;
		}
		return null;
	}

	/**
	 * 多个字段，<=1条结果
	 * 
	 * @param tableName
	 *            对应要查询的表
	 * @param fields
	 *            要查询的字段,用逗号隔开
	 * @param condition
	 *            查询条件
	 * @return fields对应的数据
	 */
	public Object[] getFieldsDataByCondition(String tableName, String fields, String condition) {
		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append(fields);
		sql.append(" FROM ");
		sql.append(tableName);
		sql.append(" WHERE ");
		sql.append(condition);
		String[] strs = fields.split(",");
		int length = strs.length;
		List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql.toString());
		Iterator<Map<String, Object>> iter = list.iterator();
		Object[] results = new Object[length];
		Object value = null;
		if (iter.hasNext()) {
			Map<String, Object> m = iter.next();
			for (int i = 0; i < length; i++) {
				value = m.get(strs[i].toUpperCase());
				if (value != null && value.getClass().getSimpleName().toUpperCase().equals("TIMESTAMP")) {
					Timestamp time = (Timestamp) value;
					try {
						value = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD_HMS);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				results[i] = value;
			}
			return results;
		}
		return null;
	}

	/**
	 * 多个字段，多条结果
	 * 
	 * @param tableName
	 *            对应要查询的表
	 * @param fields
	 *            要查询的字段集合
	 * @param condition
	 *            查询条件
	 * @return fields对应的数据
	 */
	public List<Object[]> getFieldsDatasByCondition(String tableName, String[] fields, String condition) {
		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append(BaseUtil.parseArray2Str(fields, ","));
		sql.append(" FROM ");
		sql.append(tableName);
		sql.append(" WHERE ");
		sql.append(condition);
		List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql.toString());
		Iterator<Map<String, Object>> iter = list.iterator();
		List<Object[]> datas = new ArrayList<Object[]>();
		Object value = null;
		Map<String, Object> m = null;
		Object[] results = null;
		int length = fields.length;
		while (iter.hasNext()) {
			results = new Object[length];
			m = iter.next();
			for (int i = 0; i < length; i++) {
				value = m.get(fields[i].toUpperCase());
				if (value != null && value.getClass().getSimpleName().toUpperCase().equals("TIMESTAMP")) {
					Timestamp time = (Timestamp) value;
					try {
						value = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD_HMS);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				results[i] = value;
			}
			datas.add(results);
		}
		return datas;
	}

	public List<JSONObject> getFieldsJSONDatasByCondition(String tableName, String[] fields, String condition) {
		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append(BaseUtil.parseArray2Str(fields, ","));
		sql.append(" FROM ");
		sql.append(tableName);
		sql.append(" WHERE ");
		sql.append(condition);
		List<JSONObject> datas = new ArrayList<JSONObject>();
		JSONObject obj = null;
		Object value = null;
		SqlRowList sl = queryForRowSet(sql.toString());
		while (sl.next()) {
			obj = new JSONObject();
			for (int i = 0; i < fields.length; i++) {
				value = sl.getObject(i + 1);
				if (value != null && "TIMESTAMP".equals(value.getClass().getSimpleName().toUpperCase())) {
					Timestamp time = (Timestamp) value;
					value = DateUtil.parseDateToString(new Date(time.getTime()), "yyyy-MM-dd HH:mm:ss");
				}
				obj.put(fields[i], value);
			}
			datas.add(obj);
		}
		return datas;
	}

	/**
	 * @param tableName
	 *            对应要查询的表
	 * @param condition
	 *            查询条件
	 * @return Count
	 */
	public int getCountByCondition(String tableName, String condition) {
		StringBuffer sql = new StringBuffer("SELECT count(1) FROM ");
		sql.append(tableName);
		sql.append(" WHERE ");
		sql.append(condition);
		SqlRowList srs = queryForRowSet(sql.toString());
		if (srs.next()) {
			return srs.getInt(1);
		} else {
			try {
				throw new Exception("Condition:" + condition + " is wrong!");
			} catch (Exception e) {
				return -1;
			}
		}
	}

	/**
	 * if resultSet is null return true
	 */
	public boolean checkByCondition(String caller, String condition) {
		int count = getCountByCondition(caller, condition);
		if (count == 0) {
			return true;
		}
		return false;
	}

	/**
	 * if resultSet not null return true
	 */
	public boolean checkIf(String table, String condition) {
		int count = getCountByCondition(table, condition);
		if (count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 修改操作
	 * 
	 * @param tableName
	 *            表
	 * @param update
	 *            修改内容
	 * @param condition
	 *            条件语句
	 */
	public void updateByCondition(String tableName, String update, String condition) {
		StringBuffer sb = new StringBuffer("UPDATE ");
		sb.append(tableName);
		sb.append(" SET ");
		sb.append(update);
		sb.append(" WHERE ");
		sb.append(condition);
		execute(sb.toString());
	}

	/**
	 * 修改单据为已审核
	 * 
	 * @param tableName
	 * @param condition
	 * @param statusField
	 * @param statusCodeField
	 */
	public void audit(String tableName, String condition, String statusField, String statusCodeField) {
		Status status = Status.AUDITED;
		updateByCondition(tableName, statusCodeField + "='" + status.code() + "'," + statusField + "='" + status.display() + "'", condition);
	}

	/**
	 * 修改单据为已审核(包括审核人+审核日期)
	 * 
	 * @param tableName
	 * @param condition
	 * @param statusField
	 * @param statusCodeField
	 * @param auditdateField
	 * @param auditorField
	 */
	public void audit(String tableName, String condition, String statusField, String statusCodeField, String auditdateField,
			String auditorField) {
		Status status = Status.AUDITED;
		Employee employee = SystemSession.getUser();
		updateByCondition(tableName, statusCodeField + "='" + status.code() + "'," + statusField + "='" + status.display() + "',"
				+ auditdateField + "=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + "," + auditorField + "='"
				+ (employee == null ? "" : employee.getEm_name()) + "'", condition);
	}

	/**
	 * 修改单据为已提交
	 * 
	 * @param tableName
	 * @param condition
	 * @param statusField
	 * @param statusCodeField
	 */
	public void submit(String tableName, String condition, String statusField, String statusCodeField) {
		Status status = Status.COMMITED;
		updateByCondition(tableName, statusCodeField + "='" + status.code() + "'," + statusField + "='" + status.display() + "'", condition);
	}

	/**
	 * 修改单据为在录入
	 * 
	 * @param tableName
	 * @param condition
	 * @param statusField
	 * @param statusCodeField
	 */
	public void resOperate(String tableName, String condition, String statusField, String statusCodeField) {
		Status status = Status.ENTERING;
		updateByCondition(tableName, statusCodeField + "='" + status.code() + "'," + statusField + "='" + status.display() + "'", condition);
	}

	/**
	 * 反审核操作
	 * 
	 * @param tableName
	 * @param condition
	 * @param statusField
	 * @param statusCodeField
	 * @param auditdateField
	 * @param auditorField
	 */
	public void resAudit(String tableName, String condition, String statusField, String statusCodeField, String auditorField,
			String auditdateField) {
		Status status = Status.ENTERING;
		updateByCondition(tableName, statusCodeField + "='" + status.code() + "'," + statusField + "='" + status.display() + "',"
				+ auditorField + "=null," + auditdateField + "=null", condition);
	}

	/**
	 * 修改单据为已打印
	 * 
	 * @param tableName
	 * @param condition
	 * @param statusField
	 * @param statusCodeField
	 */
	public void print(String tableName, String condition, String statusField, String statusCodeField) {
		Status status = Status.PRINTED;
		updateByCondition(tableName, statusCodeField + "='" + status.code() + "'," + statusField + "='" + status.display() + "'", condition);
	}

	/**
	 * 修改单据为已禁用
	 * 
	 * @param tableName
	 * @param condition
	 * @param statusField
	 * @param statusCodeField
	 */
	public void banned(String tableName, String condition, String statusField, String statusCodeField) {
		Status status = Status.DISABLE;
		updateByCondition(tableName, statusCodeField + "='" + status.code() + "'," + statusField + "='" + status.display() + "'", condition);
	}

	public String getformFieldsbyTable(String Table) {
		String sql = "SELECT fd_field from FormDetail where fd_table='" + Table + "' order by fd_detno";
		List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
		Iterator<Map<String, Object>> iter = list.iterator();
		List<String> fields = new ArrayList<String>();
		Map<String, Object> m = null;
		while (iter.hasNext()) {
			m = iter.next();
			fields.add(m.get("FD_FIELD").toString());
		}
		return BaseUtil.parseList2Str(fields, ",", true);
	}

	/**
	 * 记录操作
	 */
	public void logMessage(MessageLog messageLog) {
		execute(messageLog.getSql());
	}

	/**
	 * 获取编号序列
	 * 
	 * @param myTable
	 *            Caller
	 * @param thisType
	 *            2
	 */
	public synchronized String sGetMaxNumber(String myTable, int thisType) {
	/*	if ("true".equals(BaseUtil.getXmlSetting("group"))) {
			boolean isBase = checkIf("basedataset", "bds_caller='" + myTable + "' and nvl(bds_editable,0)=1");
			if (isBase) {
				// 集团版基础资料(客户，供应商...)统一到资料中心取号
				String dataCenter = BaseUtil.getXmlSetting("dataSob");
				return callProcedure(dataCenter + ".Sp_GetMaxNumber", new Object[] { myTable, thisType });
			}
		}*/
		return callProcedure("Sp_GetMaxNumber", new Object[] { myTable, thisType });
	}

	/**
	 * 调用存储过程 无返回值
	 * 
	 * @param procedureName
	 *            存储过程名称
	 * @param args
	 *            参数
	 */
	public void procedure(String procedureName, Object[] args) {
		StringBuffer sql = new StringBuffer("{call ").append(procedureName).append("(");
		for (int i = 0; i < args.length; i++) {
			if (i > 0) {
				sql.append(",");
			}
			sql.append("?");
		}
		sql.append(")}");
		getJdbcTemplate().update(sql.toString(), args);
	}

	/**
	 * 调用存储过程
	 * 
	 * @param procedureName
	 *            存储过程名称
	 * @param args
	 *            参数
	 * @return varchar类型结果
	 */
	public List<String> callProcedureWithOut(final String procedureName, final Object[] args, final Integer[] inIndex,
			final Integer[] outIndex) {

		StringBuffer sql = new StringBuffer("{call " + procedureName + "(");

		for (int i = 0; i < inIndex.length + outIndex.length; i++) {
			if (sql.toString().contains("?")) {
				sql.append(",?");
			} else {
				sql.append("?");
			}
		}
		sql.append(")}");

		List<String> listR = getJdbcTemplate().execute(sql.toString(), new CallableStatementCallback<List<String>>() {

			@Override
			public List<String> doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				for (int i = 0; i < inIndex.length; i++) {
					cs.setObject(inIndex[i], args[i]);
				}

				for (int i = 0; i < outIndex.length; i++) {
					cs.registerOutParameter(outIndex[i], java.sql.Types.VARCHAR);
				}

				cs.execute();
				List<String> list = new ArrayList<String>();
				for (int i = 0; i < outIndex.length; i++) {

					list.add(cs.getString(outIndex[i]));
				}

				return list;
			}
		});
		return listR;

	}

	/**
	 * 调用存储过程
	 * 
	 * @param procedureName
	 *            存储过程名称
	 * @param args
	 *            参数
	 * @return varchar类型结果
	 */
	public String callProcedure(final String procedureName, final Object... args) {
		try {
			return getJdbcTemplate().execute(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection conn) throws SQLException {
					StringBuffer storedProcName = new StringBuffer("{call ");
					int i = 0;
					storedProcName.append(procedureName + "(");
					for (i = 0; i < args.length; i++) {
						if (storedProcName.toString().contains("?")) {
							storedProcName.append(",");
						}
						storedProcName.append("?");
					}
					if (storedProcName.toString().contains("?")) {
						storedProcName.append(",");
					}
					storedProcName.append("?");
					storedProcName.append(")}");
					CallableStatement cs = conn.prepareCall(storedProcName.toString());
					for (i = 0; i < args.length; i++) {
						cs.setObject(i + 1, args[i]);
					}
					cs.registerOutParameter(args.length + 1, java.sql.Types.VARCHAR);
					return cs;
				}
			}, new CallableStatementCallback<String>() {
				@Override
				public String doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
					cs.execute();
					return cs.getString(args.length + 1);
				}

			});
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
		}
		return null;
	}

	/**
	 * 调用存储过程
	 * 
	 * @param procedureName
	 *            存储过程名称
	 * @param cls
	 *            返回结果java类型
	 * @param sqlType
	 *            返回结果的sql类型
	 * @param args
	 *            参数
	 * @return varchar类型结果
	 */
	public <T> T callbackProcedure(final String procedureName, final Class<T> cls, final int sqlType, final Object... args) {
		try {
			return getJdbcTemplate().execute(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection conn) throws SQLException {
					StringBuffer storedProcName = new StringBuffer("{call ");
					int i = 0;
					storedProcName.append(procedureName + "(");
					for (i = 0; i < args.length; i++) {
						if (storedProcName.toString().contains("?")) {
							storedProcName.append(",");
						}
						storedProcName.append("?");
					}
					if (storedProcName.toString().contains("?")) {
						storedProcName.append(",");
					}
					storedProcName.append("?");
					storedProcName.append(")}");
					CallableStatement cs = conn.prepareCall(storedProcName.toString());
					for (i = 0; i < args.length; i++) {
						cs.setObject(i + 1, args[i]);
					}
					cs.registerOutParameter(args.length + 1, sqlType);
					return cs;
				}
			}, new CallableStatementCallback<T>() {
				@SuppressWarnings("unchecked")
				@Override
				public T doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
					cs.execute();
					// do not use method: getObject(paramInt, paramClass)
					return (T) cs.getObject(args.length + 1);
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError(e.getMessage());
		}
		return null;
	}

	/**
	 * 查询结果集
	 * 
	 * @param sql
	 *            查询语句
	 */
	public SqlRowList queryForRowSet(String sql) {
		SqlRowList rs = new SqlRowList();
		rs.setResultList(getJdbcTemplate().queryForList(sql));
		return rs;
	}

	/**
	 * 查询结果集
	 * 
	 * @param sql
	 *            查询语句
	 * @param arg
	 *            参数
	 */
	public SqlRowList queryForRowSet(String sql, Object arg) {
		SqlRowList rs = new SqlRowList();
		rs.setResultList(getJdbcTemplate().queryForList(sql, arg));
		return rs;
	}

	/**
	 * 查询结果集
	 * 
	 * @param sql
	 *            查询语句
	 * @param args
	 *            参数
	 */
	public SqlRowList queryForRowSet(String sql, Object... args) {
		SqlRowList rs = new SqlRowList();
		rs.setResultList(getJdbcTemplate().queryForList(sql, args));
		return rs;
	}

	/**
	 * 查询结果集
	 * 
	 * @param sql
	 *            查询语句
	 * @param args
	 *            参数
	 */
	public SqlRowList queryForRowSet(String sql, List<Object> args) {
		SqlRowList rs = new SqlRowList();
		rs.setResultList(getJdbcTemplate().queryForList(sql, args));
		return rs;
	}

	private void saveRecycle(final JSONObject recycle) {
		try {
			String sql = "INSERT INTO Recycles(re_caller,re_data,re_keyvalue,re_detailvalue,re_emcode,re_emname,re_code)"
					+ " VALUES(?,?,?,?,?,?,?)";
			getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
				@Override
				protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException, DataAccessException {
					ps.setString(1, recycle.getString("re_caller"));
					lob.setClobAsString(ps, 2, recycle.getString("re_data"));
					ps.setObject(3, recycle.get("re_keyvalue"));
					ps.setObject(4, recycle.get("re_detailvalue"));
					ps.setObject(5, recycle.get("re_emcode"));
					ps.setObject(6, recycle.get("re_emname"));
					ps.setObject(7, recycle.get("re_code"));
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除前 数据存放到回收站 rf_keyvalue和rg_keyvalue均为主表ID值,通过ID值关联
	 */
	public void recycleAll(String caller, Integer id, Employee employee) {
		String emcode = employee.getEm_code();
		String emname = employee.getEm_name();
		JSONObject recycle = null;
		Object[] objs = getFieldsDataByCondition("Form",
				"fo_table,fo_keyfield,fo_detailtable,fo_detailmainkeyfield,fo_detailkeyfield,fo_codefield", "fo_caller='" + caller + "'");
		if (objs != null) {
			// 主表
			Object fTab = objs[0];
			Object fKey = objs[1];
			if (fTab != null && fKey != null) {
				StringBuffer sql = new StringBuffer("SELECT * FROM ");
				sql.append(fTab);
				sql.append(" WHERE ");
				sql.append(fKey);
				sql.append("=");
				sql.append(id);
				SqlRowList f = queryForRowSet(sql.toString());
				Object cKey = objs[5];
				String code = "";
				if (f.next()) {
					if (cKey != null) {
						code = f.getString(cKey.toString());
					}
					String j = f.getJSON();
					recycle = new JSONObject();
					recycle.put("re_caller", caller);
					recycle.put("re_data", j);
					recycle.put("re_keyvalue", id);
					recycle.put("re_detailvalue", null);
					recycle.put("re_emcode", emcode);
					recycle.put("re_emname", emname);
					recycle.put("re_code", code);
					saveRecycle(recycle);
					// 从表
					fTab = objs[2];
					fKey = objs[3];
					String kTab = null;
					String kField = null;
					String dKey = String.valueOf(objs[4]);
					if (fTab != null && fKey != null && dKey != null) {
						kTab = fTab.toString().split(" ")[0];
						kField = fKey.toString().split(" ")[0];
						sql = new StringBuffer("SELECT * FROM ");
						sql.append(fTab);
						sql.append(" WHERE ");
						if (kField.toString().contains(".")) {
							sql.append(kField);
						} else {
							sql.append(kTab + "." + kField);
						}
						sql.append("=");
						sql.append(id);
						f = queryForRowSet(sql.toString());
						while (f.next()) {
							j = f.getJSON();
							recycle = new JSONObject();
							recycle.put("re_caller", caller);
							recycle.put("re_data", j);
							recycle.put("re_keyvalue", id);
							recycle.put("re_detailvalue", f.getInt(dKey));
							recycle.put("re_emcode", emcode);
							recycle.put("re_emname", emname);
							recycle.put("re_code", code);
							saveRecycle(recycle);
						}
					}
				}
			}
		}
	}

	/**
	 * 删除明细前数据存放到回收站 在有配置的情况下，rg_keyvalue为主表ID值，否则，为明细ID值
	 */
	public void recycle(String caller, Integer id, Employee employee) {
		String emcode = employee.getEm_code();
		String emname = employee.getEm_name();
		JSONObject recycle = null;
		// 先根据caller，到Form取配置
		Object[] objs = getFieldsDataByCondition("Form", "fo_detailtable,fo_detailkeyfield,fo_detailmainkeyfield,fo_codefield",
				"fo_caller='" + caller + "'");
		if (objs != null) {// 如果有配置
			Object fTab = objs[0];
			Object fKey = objs[1];
			Object mKey = objs[2];
			String kTab = null;
			String kField = null;
			if (fTab != null && fKey != null && mKey != null) {
				Object cKey = objs[3];
				String code = "";
				kTab = fTab.toString().split(" ")[0];
				kField = fKey.toString().split(" ")[0];
				StringBuffer sql = new StringBuffer("SELECT * FROM ");
				sql.append(fTab);
				sql.append(" WHERE ");
				if (kField.toString().contains(".")) {
					sql.append(kField);
				} else {
					sql.append(kTab + "." + kField);
				}
				sql.append("=");
				sql.append(id);
				SqlRowList f = queryForRowSet(sql.toString());
				if (f.next()) {
					if (cKey != null) {
						code = f.getString(cKey.toString());
					}
					String j = f.getJSON();
					int key = f.getInt(mKey.toString());
					recycle = new JSONObject();
					recycle.put("re_caller", caller);
					recycle.put("re_data", j);
					recycle.put("re_keyvalue", key);
					recycle.put("re_detailvalue", id);
					recycle.put("re_emcode", emcode);
					recycle.put("re_emname", emname);
					recycle.put("re_code", code);
					saveRecycle(recycle);
				}
			}
		} else {// 如果Form没有配置，到DetailGrid取对应配置
			objs = getFieldsDataByCondition("Detailgrid", "dg_table,dg_field", "dg_caller='" + caller + "' AND dg_logictype='keyField'");
			if (objs != null) {
				Object fTab = objs[0];
				Object fKey = objs[1];
				String kTab = null;
				String kField = null;
				if (fTab != null && fKey != null) {
					kTab = fTab.toString().split(" ")[0];
					kField = fKey.toString().split(" ")[0];
					StringBuffer sql = new StringBuffer("SELECT * FROM ");
					sql.append(fTab);
					sql.append(" WHERE ");
					if (kField.toString().contains(".")) {
						sql.append(kField);
					} else {
						sql.append(kTab + "." + kField);
					}
					sql.append("=");
					sql.append(id);
					SqlRowList f = queryForRowSet(sql.toString());
					if (f.next()) {
						String j = f.getJSON();
						objs = getFieldsDataByCondition("Detailgrid", "dg_field", "dg_caller='" + caller + "' AND dg_logictype='mainField'");
						recycle = new JSONObject();
						recycle.put("re_caller", caller);
						recycle.put("re_data", j);
						recycle.put("re_detailvalue", id);
						recycle.put("re_emcode", emcode);
						recycle.put("re_emname", emname);
						recycle.put("re_code", null);
						if (objs != null && objs[0] != null) {// 如果DetailGrid配置有mainField
							recycle.put("re_keyvalue", f.getInt(objs[0].toString()));
						} else {
							recycle.put("re_keyvalue", null);
						}
						saveRecycle(recycle);
					}
				}
			}
		}
	}

	/**
	 * 判断是否入库
	 * 
	 * @param caller
	 * @return
	 */
	public boolean isProdIn(String caller) {
		SettingDao settingDao = (SettingDao) ContextUtil.getBean("SettingDao");
		Map<String, String> ios = settingDao.getInOutTypes();
		return ios != null && ("IN".equals(ios.get(caller)) || "-OUT".equals(ios.get(caller)));
	}

	/**
	 * 获取入库单批号
	 */
	public String getBatchcode(String caller) {
		if (isProdIn(caller)) {
			/**
			 * procedure utl_rseq
			 * 
			 * @param seq_name
			 *            序列名，支持自动创建，格式{@code seq_name}_rseq
			 * @param seq_rule
			 *            {prefix}/{time format}/{length of pad and nextval}
			 * @return seq_val
			 */
			return callProcedure("utl_rseq", "BatchCode", "/yyMMdd/5");
		}
		return null;
	}

	/**
	 * 根据物料中的有效期天数更新有效期止
	 */
	public void getEndDate(String caller, Object id) {
		SqlRowList rs = queryForRowSet("select ds_inorout from DOCUMENTSETUP where ds_table=?", caller);
		if (rs.next()) {
			String Code = rs.getObject("ds_inorout").toString();
			if (Code.equals("IN") || Code.equals("-OUT")) {
				execute("update prodiodetail set pd_replydate=pd_prodmadedate + (select nvl(pr_validdays,0) from product where  pd_prodcode=pr_code) where pd_piid=? and pd_prodmadedate is not null",
						id);
			}
		}
	}

	/**
	 * 调用存储过程
	 * 
	 * @param procedureName
	 *            存储过程名称
	 * @param args
	 *            参数
	 * @param out
	 *            inout类型参数序号
	 * @return varchar类型结果
	 */
	public synchronized String callProcedure(final String procedureName, final Object[] args, final int out) {
		try {
			return getJdbcTemplate().execute(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection conn) throws SQLException {
					StringBuffer storedProcName = new StringBuffer("{call ");
					int i = 0;
					storedProcName.append(procedureName + "(");
					for (i = 0; i < args.length; i++) {
						if (storedProcName.toString().contains("?")) {
							storedProcName.append(",");
						}
						storedProcName.append("?");
					}
					storedProcName.append(")}");
					CallableStatement cs = conn.prepareCall(storedProcName.toString());
					for (i = 0; i < args.length; i++) {
						cs.setObject(i + 1, args[i]);
					}
					cs.registerOutParameter(out, java.sql.Types.VARCHAR);
					return cs;
				}
			}, new CallableStatementCallback<String>() {
				@Override
				public String doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
					cs.execute();
					return cs.getString(out);
				}

			});
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
		}
		return null;
	}

	public void saveClob(final String tabName, final String clobField, final String clobStr, final String condition) {
		try {
			StringBuffer sb = new StringBuffer("update ").append(tabName).append(" set ").append(clobField).append("=? where ")
					.append(condition);
			getJdbcTemplate().execute(sb.toString(), new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
				@Override
				protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException, DataAccessException {
					lob.setClobAsString(ps, 1, clobStr);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveClob(final String tabName, final List<String> clobFields, final List<String> clobStrs, final String condition) {
		if (clobFields.size()<1) {
			return ;
		}
		try {
			StringBuffer sb = new StringBuffer("update ").append(tabName).append(" set ");
			for (int i = 0; i < clobFields.size(); i++) {
				sb.append(clobFields.get(i) + "=?");
				if (i < clobFields.size() - 1)
					sb.append(",");
			}
			sb.append(" where ").append(condition);
			getJdbcTemplate().execute(sb.toString(), new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
				@Override
				protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException, DataAccessException {
					for (int i = 0; i < clobStrs.size(); i++) {
						String val = clobStrs.get(i);
						if (val.contains("'")) {
							val = val.replaceAll("'", "''");
						} else if (val.contains("%n")) {
							val = val.replaceAll("%n", "\n");
						}
						lob.setClobAsString(ps, i + 1, val);
					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 转单据通用方法 Map<String,Object> baseData 前台传入的数据 String 对应
	 * TurnTableSet的TTS_FORMCOLUMN String turnCaller 转单的caller 关联 TurnTableSet 表
	 * int baseId 转单原始单据的主表id config config[0]-->类型 ADD/UPDATE config[1]
	 * -->如果是UPDATE UPDATE 的主键名 转单据通用方法 Map<String,Object> baseData 前台传入的数据
	 * String 对应 TurnTableSet的TTS_FORMCOLUMN String turnCaller 转单的caller 关联
	 * TurnTableSet 表 int baseId 转单原始单据的主表id config config[0]-->类型 ADD/UPDATE
	 * config -->如果是UPDATE UPDATE 的主键名
	 * 
	 */
	public Map<String, Object> turnBill(Map<String, Object> baseData, String turnCaller, int baseId, String[] config) {
		String language = SystemSession.getLang();
		// 传出返回值得map
		Map<String, Object> map = new HashMap<String, Object>();

		if ((config.length < 1 ? "ADD" : config[0]).equals("ADD")) {

			// 得到转单配置表的相关配置数据
			List<Object[]> list = getFieldsDatasByCondition("TURNTABLESET", new String[] { "TTS_DETNO", "TTS_FROMTABLE", "TTS_FROMCOLUMN",
					"TTS_TOTABLE", "TTS_TOCOLUMN", "TTS_ISTVALUE", "TTS_KEYFIELD" }, "TTS_CALLER='" + turnCaller + "'");
			// 插入TOTABLE的 insert语句的列名
			String insertSql = "INSERT INTO ";
			// 插入TOTABLE的insert语句的value值
			String valuesSql = "VALUES (";
			// 查询FORMTABLE的查询语句
			String selectSql = "SELECT ";
			// 配置的formtable的表明 TTS_FORMTABLE
			String formTable = "";
			// 配置的 formtable的主键名 TTS_KEYFIELD
			String keyField = "";

			// 和insert语句中参数?对应的值
			Object[] itemo = new Object[list.size()];
			int i = 0;
			int k = 0;

			if (list.size() > 0) {
				for (Object[] o : list) {
					if (i == 0) {
						insertSql = insertSql + o[3].toString() + "(" + o[4].toString();
						valuesSql = valuesSql + "?";
						formTable = o[1].toString();
						keyField = o[6].toString();
						if (Integer.parseInt(o[5].toString()) == -1) {
							selectSql = selectSql + o[2].toString();
							k++;
						}

					} else {
						insertSql = insertSql + "," + o[4].toString();
						valuesSql = valuesSql + "," + "?";
						if (Integer.parseInt(o[5].toString()) == -1) {
							if (k == 0) {
								selectSql = selectSql + o[2].toString();
							} else {
								selectSql = selectSql + "," + o[2].toString();
							}
							k++;
						}
					}

					i++;
				}
			}

			insertSql = insertSql + ") ";
			valuesSql = valuesSql + ") ";
			insertSql = insertSql + valuesSql;

			selectSql = selectSql + " FROM " + formTable + " WHERE " + keyField + "='" + baseId + "'";

			SqlRowList rs = queryForRowSet(selectSql);
			while (rs.next()) {
				if (list.size() > 0) {
					int j = 0;
					for (Object[] o : list) {
						if (Integer.parseInt(o[5].toString()) == -1) {
							itemo[j] = rs.getObject(o[2].toString());

						} else if (Integer.parseInt(o[5].toString()) == 0) {
							itemo[j] = baseData.get(o[2].toString());
						} else {
							itemo[j] = decodeDefaultValue(o[2], language);
						}

						j++;
					}
				}
			}
			execute(insertSql, itemo);
		} else if (config[0].equals("UPDATE")) {

			// 得到转单配置表的相关配置数据
			List<Object[]> list = getFieldsDatasByCondition("TURNTABLESET", new String[] { "TTS_DETNO", "TTS_FROMTABLE", "TTS_FROMCOLUMN",
					"TTS_TOTABLE", "TTS_TOCOLUMN", "TTS_ISTVALUE", "TTS_KEYFIELD" }, "TTS_CALLER='" + turnCaller + "'");
			// 插入TOTABLE的 insert语句的列名
			String updateSql = "UPDATE ";
			// 查询FORMTABLE的查询语句
			String selectSql = "SELECT ";
			// 配置的formtable的表明 TTS_FORMTABLE
			String formTable = "";
			// 配置的 formtable的主键名 TTS_KEYFIELD
			String keyField = "";

			String updateid = "";

			// 和insert语句中参数?对应的值
			Object[] itemo = new Object[list.size()];
			int i = 0;
			int k = 0;

			if (list.size() > 0) {
				for (Object[] o : list) {
					if (config[1].toString().equals(o[4].toString())) {
						if (Integer.parseInt(o[5].toString()) == 0) {
							updateid = baseData.get(o[2].toString()).toString();
						}

					}

					if (i == 0) {
						updateSql = updateSql + o[3].toString() + " SET " + o[4].toString() + "=?";
						formTable = o[1].toString();
						keyField = o[6].toString();

						if (Integer.parseInt(o[5].toString()) == -1) {
							selectSql = selectSql + o[2].toString();
							k++;
						}

					} else {
						updateSql = updateSql + "," + o[4].toString() + "=?";
						if (Integer.parseInt(o[5].toString()) == -1) {
							if (k == 0) {
								selectSql = selectSql + o[2].toString();
							} else {
								selectSql = selectSql + "," + o[2].toString();
							}
							k++;
						}
					}

					i++;
				}
			}

			updateSql = updateSql + " WHERE " + config[1] + "='" + updateid + "'";
			selectSql = selectSql + " FROM " + formTable + " WHERE " + keyField + "='" + baseId + "'";

			SqlRowList rs = queryForRowSet(selectSql);
			while (rs.next()) {
				if (list.size() > 0) {
					int j = 0;
					for (Object[] o : list) {
						if (Integer.parseInt(o[5].toString()) == -1) {
							itemo[j] = rs.getObject(o[2].toString());

						} else if (Integer.parseInt(o[5].toString()) == 0) {
							itemo[j] = baseData.get(o[2].toString());
						} else {
							itemo[j] = decodeDefaultValue(o[2], language);
						}

						j++;
					}
				}
			}
			execute(updateSql, itemo);
		}

		return map;
	}

	/**
	 * 将数据库里面的defaultvalue转化成实际要显示的值
	 * 
	 * @param value
	 *            formDetail.getFd_defaultvalue()
	 */
	public Object decodeDefaultValue(Object value, String language) {
		if (value != null && !value.equals("null")) {
			String val = value.toString();
			if (val.contains("getCurrentDate()")) {
				return new Date();
			} else if (val.contains("getLocal(")) {
				Object obj = BaseUtil.getLocalMessage(val.substring(val.indexOf("(") + 1, val.lastIndexOf(")")), language);
				return (obj == null) ? "" : obj;
			}
			return value;
		} else {
			return "";
		}
	}

	/**
	 * checkSQL
	 * */
	public boolean checkSQL(String sql) {
		try {
			execute(sql);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * 删除、反审核等敏感操作前，关联表数据检查
	 * 
	 * @param tableName
	 *            表名
	 * @param keyData
	 *            主键值
	 * @param cascadeType
	 *            操作
	 * @return
	 */
	public void relationCheck(String tableName, Object keyData, String cascadeType) {
		String checkError = callProcedure("tablerelation.relationCheck", new Object[] { tableName, String.valueOf(keyData), cascadeType });
		if (checkError != null)
			BaseUtil.showError("无法完成操作！存在关联的数据：" + checkError);
	}

	/**
	 * 删除前，关联表数据检查
	 * 
	 * @param tableName
	 *            表名
	 * @param keyData
	 *            主键值
	 * @return
	 */
	public void delCheck(String tableName, Object keyData) {
		relationCheck(tableName, keyData, "DELETE");
	}

	/**
	 * 反审核前，关联表数据检查
	 * 
	 * @param tableName
	 *            表名
	 * @param keyData
	 *            主键值
	 * @return
	 */
	public void resAuditCheck(String tableName, Object keyData) {
		relationCheck(tableName, keyData, "RESAUDIT");
	}

	/**
	 * 检测当前日期所在期间是否结账
	 * 
	 * @param type
	 *            期间类型
	 * @param orderdate
	 *            单据日期
	 * @return
	 */
	public void checkCloseMonth(String type, Object orderdate) {
		boolean bool = checkIf("PeriodsDetail", "pd_code='" + type + "' and pd_status=99 and pd_detno=to_char(to_date('" + orderdate
				+ "','yyyy-mm-dd hh24:mi:ss'), 'yyyymm')");
		if (bool) {
			BaseUtil.showError("单据日期所属期间已结账，不允许进行当前操作!");
		}
	}

	/**
	 * 获取限制权限
	 * */
	public String getLimitCondition(String tabSql, Integer emid) {
		String condition = "";
		try {
			if (checkIf("DataLimit", "USEABLE_=1")) {
				String tables = getJdbcTemplate()
						.queryForObject("select tablerelation.getTables('" + tabSql + "') from dual", String.class);
				tables = tables.replaceAll("TAB", "").trim();
				condition = getJdbcTemplate().queryForObject("select  GET_DATALIMITSQL(" + emid + ",'" + tables + "') FROM DUAL",
						String.class);
				return condition != null ? condition : "";
			}
			return condition;
		} catch (Exception e) {
			return condition;
		}

	}

	/***
	 * 平台传回ERP接收标准方法 content 内容 type 消息类型 noticemans 对应产生的人员名称以#号分隔
	 * 
	 * **/
	public List<String> beatchNotices(String content, String type, String noticemans, String condition, String fromcaller) {
		List<String> sqls = new ArrayList<String>();
		int pr_id = getSeqId("PAGINGRELEASE_SEQ");
		sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_context,PR_FROM,pr_caller,pr_content )values('" + pr_id
				+ "','系统管理员'," + DateUtil.parseDateToOracleString(Constant.YMD, new Date()) + ",'" + content + "','" + type + "','"
				+ fromcaller + "','" + condition + "')");
		String[] mans = (String.valueOf(noticemans)).split("#");
		for (String man : mans) {
			Object manid = getFieldDataByCondition("employee", "max(em_id)", "em_name='" + man + "'");
			int prd_id = getSeqId("PAGINGRELEASEDETAIL_SEQ");
			sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values('" + prd_id + "','" + pr_id
					+ "','" + manid + "','" + man + "')");
		}
		// 保存到历史消息表
		int IH_ID = getSeqId("ICQHISTORY_SEQ");
		sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
				+ "select "
				+ IH_ID
				+ ",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
				+ " where pr_id=" + pr_id);
		sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
				+ "select ICQHISTORYdetail_seq.nextval," + IH_ID
				+ ",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid=" + pr_id);
		return sqls;
	}

	/*
	 * 具体单据产生平台知会信息人员处理 caller 指Btob$noticeset 表的设置caller id 具体单据Id
	 */
	public void auditInsertTaskman(String caller, Integer id) {
		StringBuffer sb = new StringBuffer();
		Object[] objs = getFieldsDataByCondition(
				"Btob$NoticeSet",
				"bn_callername,bn_mainid,bn_enable,bn_torecorder,bn_toauditer,bn_toposition,bn_toother,bn_isjhy,bn_callernamedetail,bn_prodcode,bn_gridmainid",
				"bn_caller='" + caller + "'");
		if (objs != null && Integer.parseInt(objs[2].toString()) == -1) {
			// 录入人
			if (objs[3] != null) {
				Object man = getFieldDataByCondition(objs[0].toString(), objs[3].toString(), objs[1] + "=" + id);
				if (man != null) {
					sb.append(man + "#");
				}
			}
			// 审核人
			if (objs[4] != null) {
				Object man = getFieldDataByCondition(objs[0].toString(), objs[4].toString(), objs[1] + "=" + id);
				if (man != null) {
					sb.append(man + "#");
				}
			}
			// 岗位
			if (objs[5] != null) {
				String[] jobs = objs[5].toString().split("#");
				String job = "";
				for (String ss : jobs) {
					job = "'" + ss + "',";
				}
				job = job.substring(0, job.length() - 1);
				Object[] os = getFieldsDataByCondition("employee", new String[] { "em_name" }, "em_position in (" + job + ")");
				if (os != null) {
					for (Object em : os) {
						sb.append(em + "#");
					}
				}
			}
			// 物料计划员
			if (objs[7] != null && Integer.parseInt(objs[7].toString()) == -1 && objs[8] != null && objs[9] != null) {
				Object[] prodmans = getFieldsDataByCondition(objs[8] + " left join product on pr_code=" + objs[9], "pr_planner", " "
						+ objs[10] + "=" + id);
				if (prodmans != null) {
					for (Object prodman : prodmans) {
						if (prodman != null) {
							sb.append(prodman + "#");
						}
					}
				}
			}
			// 其他人
			if (objs[6] != null) {
				sb.append(objs[6]);
			}
		}
		String ems = sb.toString();
		if (ems.length() > 0) {
			String sss = StringUtil.deleteRepeats(ems, "#");
			String sqlstr = "insert into BTOB$NOTICEMAN (ID,CALLER,MANS,MANID,indate) values(BTOB$NOTICEMAN_seq.nextval,'" + caller + "','"
					+ sss + "'," + id + ",sysdate)";
			execute(sqlstr);
		}
	}

	/**
	 * @param title
	 *            信息标题 context 信息内容 recipitenter 接收人 istop 是否置顶
	 * */
	public void pagingRelease(String title, String context, Employee em, boolean istop) {
		int prId = getSeqId("PAGINGRELEASE_SEQ");
		// 保存到历史消息表
		int IH_ID = getSeqId("ICQHISTORY_SEQ");
		if (em != null) {

		} else {
			em = SystemSession.getUser();
		}
		String sqls[] = new String[] {
				"insert into pagingrelease(pr_id,pr_title,pr_context,pr_date,pr_istop,pr_releaserid,pr_releaser,pr_status) values " + "("
						+ prId + ",'" + title + "','" + context + "',sysdate," + (istop ? 1 : 0) + ",0,'管理员',0)",
				"insert into pagingreleasedetail(prd_prid,prd_recipientid,prd_recipient) VALUES(" + prId + "," + em.getEm_id() + ",'"
						+ em.getEm_id() + "')",
				"Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
						+ " select "
						+ IH_ID
						+ ",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE where pr_id="
						+ prId,
				"Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
						+ "select ICQHISTORYdetail_seq.nextval," + IH_ID
						+ ",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid=" + prId };
		execute(sqls);
	}

	public String getEnterpriseUU(String name) {
		String enuu = null;
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.getMa_b2bwebsite() != null) {
			if (name != null) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("data", name);
				try {
					Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/public/queriable/batch/members", params,
							false);
					if (response.getStatusCode() == HttpStatus.OK.value()) {
						Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
						if (backInfo.size() > 0) {
							for (String uu : backInfo.keySet()) {
								enuu = backInfo.get(uu).toString();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return enuu;
	}

	/*
	 * blob类型字段更新 tabName 表名 blobField BLOB类型字段 bytes 文件的byte数组 condition 条件
	 */
	public void saveBlob(final String tabName, final String blobField, final byte[] bytes, final String condition) {
		try {
			StringBuffer sb = new StringBuffer("update ").append(tabName).append(" set ").append(blobField).append("=? where ")
					.append(condition);
			getJdbcTemplate().execute(sb.toString(), new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
				@Override
				protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException, DataAccessException {
					lob.setBlobAsBytes(ps, 1, bytes);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取blob字段的值
	 * 
	 * @param tabName
	 * @param lobField
	 * @param condition
	 * @return
	 */
	public byte[] getBlob(final String tabName, final String lobField, final String condition) {
		try {
			StringBuffer sb = new StringBuffer("select ").append(lobField).append(" from ").append(tabName).append(" where ")
					.append(condition);
			return getJdbcTemplate().query(sb.toString(), new ResultSetExtractor<byte[]>() {

				@Override
				public byte[] extractData(ResultSet rs) throws SQLException, DataAccessException {
					if (rs.next()) {
						return lobHandler.getBlobAsBytes(rs, 1);
					}
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 替换配置中使用的 session
	 * @param str
	 * @return
	 */
	public String parseEmpCondition(String str) {
		Employee employee = SystemSession.getUser();
		if (str.contains("session:em_uu")) {
			str = str.replace("session:em_uu", employee.getEm_uu().toString());
		}
		if (str.contains("session:em_id")) {
			str = str.replace("session:em_id", employee.getEm_id().toString());
		}
		if (str.contains("session:em_code")) {
			str = str.replace("session:em_code", "'" + employee.getEm_code().toString() + "'");
		}
		if (str.contains("session:em_name")) {
			str = str.replace("session:em_name", "'" + employee.getEm_name().toString() + "'");
		}
		if (str.contains("session:em_defaulthsid")) {
			str = str.replace("session:em_defaulthsid", employee.getEm_defaulthsid().toString());
		}
		if (str.contains("session:em_position")) {
			str = str.replace("session:em_position", "'" + employee.getEm_position().toString() + "'");
		}
		if (str.contains("session:em_defaultorid")) {
			str = str.replace("session:em_defaultorid", employee.getEm_defaultorid().toString());
		}
		if (str.contains("session:em_defaultorname")) {
			str = str.replace("session:em_defaultorname", "'" + employee.getEm_defaultorname().toString() + "'");
		}
		if (str.contains("session:em_depart")) {
			str = str.replace("session:em_depart", "'" + employee.getEm_depart().toString() + "'");
		}
		if (str.contains("session:em_departmentcode")) {
			str = str.replace("session:em_departmentcode", "'" + employee.getEm_departmentcode().toString() + "'");
		}
		if (str.contains("session:em_cop")) {
			str = str.replace("session:em_cop", "'" + employee.getEm_cop().toString() + "'");
		}
		return str;
	}

}
