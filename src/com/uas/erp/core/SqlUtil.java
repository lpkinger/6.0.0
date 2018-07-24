package com.uas.erp.core;

import java.io.Writer;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oracle.jdbc.OracleConnection;

import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;

/**
 * 处理一些拼sql的工具包
 * 
 * @author yingp
 * @date 2012-08-02 10:22:17
 * 
 * @since 2015-11-26
 *        <p>
 *        增加对SqlMap的操作方法
 *        </p>
 */
public class SqlUtil {

	public final static String REG_D = "\\d{4}-\\d{2}-\\d{2}";
	public final static String REG_DT = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
	public final static String REG_TS = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{1}";

	/**
	 * 根据DetailGrid配置拼Sql查询语句
	 * 
	 * @param table
	 *            表名
	 */
	private static String _getQuerySqlByDetailGrid(List<DetailGrid> detailGrids, String table, String condition, String masterName) {
		StringBuffer sb = new StringBuffer("SELECT ");
		String orderby = "";
		String field = null;
		String logic = null;
		if (masterName != null) {
			sb.append("'").append(masterName).append("' CURRENTMASTER,");
		}
		for (DetailGrid detailGrid : detailGrids) {
			field = detailGrid.getDg_field();
			logic = detailGrid.getDg_logictype();
			sb.append(field);
			sb.append(",");
			if (!condition.toUpperCase().contains("ORDER BY")) {
				if (field.contains("_detno") || field.contains("_sequence") || (logic != null && "detno".equals(logic))) {
					if (field.contains(" ")) {
						field = field.substring(0, field.indexOf(" "));
					}
					orderby = " ORDER BY " + field;
				}
			}
		}
		String str = sb.substring(0, sb.length() - 1) + " FROM " + table;
		return condition.equals("") ? str : str + " where " + condition + orderby;
	}

	/**
	 * 根据DetailGrid配置拼Sql查询语句
	 * 
	 * @param table
	 *            表名
	 */
	private static String _getQuerySqlByDetailGrid(List<DetailGrid> detailGrids, String table, String condition, String masterName,
			Integer start, Integer end) {
		if (end == null)
			return _getQuerySqlByDetailGrid(detailGrids, table, condition, masterName);
		StringBuffer fieldsStr = new StringBuffer("");
		String orderby = "";
		String field = null;
		String logic = null;
		if (masterName != null) {
			fieldsStr.append("'").append(masterName).append("' CURRENTMASTER,");
		}
		for (DetailGrid detailGrid : detailGrids) {
			field = detailGrid.getDg_field();
			logic = detailGrid.getDg_logictype();
			fieldsStr.append(field);
			fieldsStr.append(",");
			if (!condition.toUpperCase().contains("ORDER BY")) {
				if (field.contains("_detno") || field.contains("_sequence") || (logic != null && "detno".equals(logic))) {
					if (field.contains(" ")) {
						field = field.substring(0, field.indexOf(" "));
					}
					orderby = " ORDER BY " + field;
				}
			}
		}
		condition = "".equals(condition) ? "" : " WHERE " + condition;
		String longFieldsStr = fieldsStr.substring(0, fieldsStr.length() - 1);
		StringBuffer sb = new StringBuffer("select * from (select TT.*, ROWNUM rn from (select ");
		sb.append(longFieldsStr);
		sb.append(" from ");
		sb.append(table);
		sb.append(" ");
		sb.append(condition);
		sb.append(" ");
		sb.append(orderby);
		sb.append(" )TT where ROWNUM <= ");
		sb.append(end);
		sb.append(") where rn >= ");
		sb.append(start);
		return sb.toString();
	}

	public static String getQuerySqlByDetailGrid(List<DetailGrid> detailGrids, String table, String condition, Employee employee,
			Integer start, Integer end) {
		Master master = employee == null ? null : employee.getCurrentMaster();
		if (master == null || master.getMa_type() == 3 || master.getMa_soncode() == null) {
			return _getQuerySqlByDetailGrid(detailGrids, table, condition, null, start, end);
		}
		String caller = detailGrids.get(0).getDg_caller();
		// 集团中心,取资料中心数据
		if (master.getMa_type() == 0
				&& ("Product".equals(caller) || "Vendor".equals(caller) || "Customer".equals(caller) || "Customer!Base".equals(caller))) {
			table = getFullTableName(table, BaseUtil.getXmlSetting("dataSob"));
			return _getQuerySqlByDetailGrid(detailGrids, table, condition, null, start, end);
		}
		String[] sonCodes = master.getMa_soncode().split(",");
		String masters = employee.getEm_masters();
		masters = masters == null ? employee.getEm_master() : masters;
		List<String> usedCodes = BaseUtil.parseStr2List(masters, ",", false);
		boolean admin = "admin".equals(employee.getEm_type());
		StringBuffer sb = new StringBuffer();
		String tabName = table;
		for (String s : sonCodes) {
			if (!admin && !usedCodes.contains(s)) {
				continue;
			}
			tabName = getFullTableName(table, s);
			if (sb.length() > 0)
				sb.append(" UNION ALL ");
			sb.append(_getQuerySqlByDetailGrid(detailGrids, tabName, condition, s, start, end));
		}
		return sb.toString();
	}

	public static String getFullTableName(String tabName, String masterCode) {
		String[] strs = tabName.split("left join ");
		StringBuffer sb = new StringBuffer();
		for (int i = 0, len = strs.length; i < len; i++) {
			sb.append(masterCode).append(".").append(strs[i]);
			if (i != len - 1)
				sb.append("left join ");
		}
		return sb.toString();
	}

	/**
	 * @param formStore
	 *            form数据
	 */
	@Deprecated
	public static String getInsertSqlByFormStore(String formStore, String table, String[] otherFields, Object[] otherValues) {
		StringBuffer sb1 = new StringBuffer("INSERT into " + table + " (");
		StringBuffer sb2 = new StringBuffer(" ");
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(formStore);
		Set<Object> keys = map.keySet();
		for (Object key : keys) {
			String field = (String) key;
			Object value = map.get(key);
			if (otherFields != null) {
				for (int i = 0, len = otherFields.length; i < len; i++) {
					if (field.equals(otherFields[i])) {// 字段重复了哦
						value = otherValues[i];// 优先选用传递过来的value
					}
				}
			}
			sb1.append(field);
			sb1.append(",");
			if (value != null) {
				String val = value.toString();
				if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
					sb2.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
				} else if (val.matches(REG_DT)) {
					sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
				} else if (val.matches(REG_TS)) {
					sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
				} else if ("null".equals(val)) {
					sb2.append("null");
				} else if ("".equals(val.trim())) {
					sb2.append("''");
				} else if (val.contains("%n")) {
					sb2.append("'" + val.replaceAll("%n", "\n") + "'");
				} else if (val.contains("'")) {
					sb2.append("'" + value.toString().replaceAll("'", "''") + "'");
				} else {
					sb2.append("'" + value + "'");
				}
			} else {
				sb2.append("null");
			}
			sb2.append(",");
		}
		if (otherFields != null) {
			for (int i = 0, len = otherFields.length; i < len; i++) {
				if (!sb1.toString().contains(otherFields[i])) {
					sb1.append(otherFields[i]);
					sb1.append(",");
					sb2.append("'" + otherValues[i] + "'");
					sb2.append(",");
				}
			}
		}
		return sb1.substring(0, sb1.length() - 1) + ") VALUES (" + sb2.substring(0, sb2.length() - 1) + ")";
	}

	/**
	 * @param map
	 *            formStore解析成的map
	 */
	public static String getInsertSqlByFormStore(Map<?, Object> map, String table, String[] otherFields, Object[] otherValues) {
		StringBuffer sb1 = new StringBuffer("INSERT into " + table + " (");
		StringBuffer sb2 = new StringBuffer(" ");
		Set<?> keys = map.keySet();
		for (Object key : keys) {
			String field = (String) key;
			Object value = map.get(key);
			for (int i = 0; i < otherFields.length; i++) {
				if (field.equals(otherFields[i])) {// 字段重复了哦
					value = otherValues[i];// 优先选用传递过来的value
				}
			}
			sb1.append(field);
			sb1.append(",");
			if (value != null) {
				String val = value.toString();
				if (value.toString().matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
					sb2.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
				} else if (value.toString().matches(REG_DT)) {
					sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
				} else if (value.toString().matches(REG_TS)) {
					sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
				} else if (value.toString().equals("null")) {
					sb2.append("null");
				} else if (val.contains("%n")) {
					sb2.append("'" + val.replaceAll("%n", "\n") + "'");
				} else if (val.contains("'")) {
					sb2.append("'" + value.toString().replaceAll("'", "''") + "'");
				} else {
					sb2.append("'" + value + "'");
				}
			} else {
				sb2.append("null");
			}
			sb2.append(",");
		}
		for (int i = 0; i < otherFields.length; i++) {
			if (!sb1.toString().contains(otherFields[i])) {
				sb1.append(otherFields[i]);
				sb1.append(",");
				sb2.append("'" + otherValues[i] + "'");
				sb2.append(",");
			}
		}
		return sb1.substring(0, sb1.length() - 1) + ") VALUES (" + sb2.substring(0, sb2.length() - 1) + ")";
	}

	/**
	 * 将grid数据从字符串形式拼成sql， 一般情况下还要加一些条件，比如，新增采购单时，加入pd_puid=?
	 * 
	 * @param
	 */
	public static List<String> getInsertSqlbyGridStore(String gridStore, String table, String otherField, Object[] otherValues) {
		List<String> sqls = new ArrayList<String>();
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(gridStore);
		int count = 0;
		StringBuffer sb1 = null;
		StringBuffer sb2 = null;
		for (Map<Object, Object> map : maps) {
			Set<Object> keys = map.keySet();
			sb1 = new StringBuffer("INSERT into " + table + " (");
			sb2 = new StringBuffer(" ");
			for (Object key : keys) {
				String field = (String) key;
				Object value = map.get(key);
				if (field.equals(otherField)) {// 字段重复了哦
					value = otherValues[count++];// 优先选用传递过来的value
				}
				sb1.append(field);
				sb1.append(",");
				if (value != null) {
					String val = value.toString();
					if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
						sb2.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
					} else if (val.matches(REG_DT)) {
						sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
					} else if (val.matches(REG_TS)) {
						sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
					} else if (value.toString().equals("null")) {
						sb2.append("null");
					} else if (val.contains("%n")) {
						sb2.append("'" + val.replaceAll("%n", "\n") + "'");
					} else if (val.contains("'")) {
						sb2.append("'" + value.toString().replaceAll("'", "''") + "'");
					} else {
						sb2.append("'" + value + "'");
					}
				} else {
					sb2.append("null");
				}
				sb2.append(",");
			}
			if (!otherField.equals("") && !sb1.toString().contains(otherField)) {
				sb1.append(otherField);
				sb1.append(",");
				sb2.append("'" + otherValues[count++] + "'");
				sb2.append(",");
			}
			sqls.add(sb1.substring(0, sb1.length() - 1) + ") VALUES (" + sb2.substring(0, sb2.length() - 1) + ")");
		}
		return sqls;
	}

	public static List<String> getInsertSqlbyGridStore(List<Map<Object, Object>> maps, String table) {
		List<String> sqls = new ArrayList<String>();
		StringBuffer sb1 = null;
		StringBuffer sb2 = null;
		for (Map<Object, Object> map : maps) {
			Set<Object> keys = map.keySet();
			sb1 = new StringBuffer("INSERT into " + table + " (");
			sb2 = new StringBuffer(" ");
			for (Object key : keys) {
				String field = (String) key;
				Object value = map.get(key);
				sb1.append(field);
				sb1.append(",");
				if (value != null) {
					String val = value.toString();
					if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
						sb2.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
					} else if (val.matches(REG_DT)) {
						sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
					} else if (val.matches(REG_TS)) {
						sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
					} else if (value.toString().equals("null")) {
						sb2.append("null");
					} else if (val.contains("%n")) {
						sb2.append("'" + val.replaceAll("%n", "\n") + "'");
					} else if (val.contains("'")) {
						sb2.append("'" + value.toString().replaceAll("'", "''") + "'");
					} else {
						sb2.append("'" + value + "'");
					}
				} else {
					sb2.append("null");
				}
				sb2.append(",");
			}
			sqls.add(sb1.substring(0, sb1.length() - 1) + ") VALUES (" + sb2.substring(0, sb2.length() - 1) + ")");
		}
		return sqls;
	}

	public static List<String> getInsertSqlbyGridStoreWithoutDate(List<Map<Object, Object>> maps, String table) {
		List<String> sqls = new ArrayList<String>();
		StringBuffer sb1 = null;
		StringBuffer sb2 = null;
		for (Map<Object, Object> map : maps) {
			Set<Object> keys = map.keySet();
			sb1 = new StringBuffer("INSERT into " + table + " (");
			sb2 = new StringBuffer(" ");
			for (Object key : keys) {
				String field = (String) key;
				Object value = map.get(key);
				sb1.append(field);
				sb1.append(",");
				if (value != null) {
					if (value.toString().equals("null")) {
						sb2.append("null");
					} else {
						sb2.append("'" + value + "'");
					}
				} else {
					sb2.append("null");
				}
				sb2.append(",");
			}
			sqls.add(sb1.substring(0, sb1.length() - 1) + ") VALUES (" + sb2.substring(0, sb2.length() - 1) + ")");
		}
		return sqls;
	}

	public static List<String> getInsertSqlbyList(List<Map<Object, Object>> maps, String table, String prif) {
		List<String> sqls = new ArrayList<String>();
		if (maps != null) {
			for (Map<Object, Object> map : maps) {
				sqls.add(getInsertSql(map, table, prif));
			}
		}
		return sqls;
	}

	/**
	 * 从map集合格式的数据中解析出insert语句
	 */
	public static String getInsertSqlByMap(Map<?, Object> map, String table, String[] otherFields, Object[] otherValues) {
		StringBuffer sb1 = new StringBuffer("INSERT into " + table + " (");
		StringBuffer sb2 = new StringBuffer(" ");
		Object value = null;
		for (Object field : map.keySet()) {
			value = map.get(field);
			if (otherFields != null) {
				for (int i = 0; i < otherFields.length; i++) {
					if (field.equals(otherFields[i])) {// 字段重复了哦
						value = otherValues[i];// 优先选用传递过来的value
					}
				}
			}
			sb1.append(field);
			sb1.append(",");
			if (value != null) {
				String val = value.toString();
				if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
					sb2.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
				} else if (val.matches(REG_DT)) {
					sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
				} else if (val.matches(REG_TS)) {
					sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
				} else if (val.equals("null")) {
					sb2.append("null");
				} else if (val.contains("%n")) {
					sb2.append("'" + val.replaceAll("%n", "\n") + "'");
				} else if (val.contains("'")) {
					sb2.append("'" + val.replaceAll("'", "''") + "'");
				} else {
					sb2.append("'" + value + "'");
				}
			} else {
				sb2.append("null");
			}
			sb2.append(",");
		}
		if (otherFields != null) {
			for (int i = 0; i < otherFields.length; i++) {
				if (!sb1.toString().contains(otherFields[i])) {
					sb1.append(otherFields[i]);
					sb1.append(",");
					sb2.append("'" + otherValues[i] + "'");
					sb2.append(",");
				}
			}
		}
		return sb1.substring(0, sb1.length() - 1) + ") VALUES (" + sb2.substring(0, sb2.length() - 1) + ")";
	}

	public static String getInsertSqlByMap(Map<?, Object> map, String table) {
		StringBuffer sb1 = new StringBuffer("INSERT into ");
		sb1.append(table);
		sb1.append(" (");
		StringBuffer sb2 = new StringBuffer(" ");
		Object value = null;
		for (Object field : map.keySet()) {
			value = map.get(field);
			sb1.append(field);
			sb1.append(",");
			if (value != null) {
				String val = value.toString();
				if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
					sb2.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
				} else if (val.matches(REG_DT)) {
					sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
				} else if (val.matches(REG_TS)) {
					sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
				} else if (val.equals("null")) {
					sb2.append("null");
				} else {
					if (val.contains("'")) {
						val = val.replaceAll("'", "''");
					} else if (val.contains("%n")) {
						val = val.replaceAll("%n", "\n");
					}
					// 针对较长字段，比如clob类型，防止ORA-01704的简单处理：切割成多个字符串连接起来
					if (val.length() > 2000) {
						sb2.append("''");
					} else if (val.length() > 1000) {
						sb2.append(StringUtil.splitAndConcat(val, 1333, "'", "'", "||"));
					} else {
						sb2.append("'" + val + "'");
					}
				}
			} else {
				sb2.append("null");
			}
			sb2.append(",");
		}
		return sb1.substring(0, sb1.length() - 1) + ") VALUES (" + sb2.substring(0, sb2.length() - 1) + ")";
	}

	public static String getInsertSql(Map<?, Object> map, String table, String keyField) {
		StringBuffer sb1 = new StringBuffer("INSERT into ");
		sb1.append(table);
		sb1.append(" (");
		StringBuffer sb2 = new StringBuffer(" ");
		Object value = null;
		for (Object field : map.keySet()) {
			value = map.get(field);
			sb1.append(field);
			sb1.append(",");
			if (field.equals(keyField)) {
				sb2.append(table).append("_seq.nextval,");
				continue;
			}
			if (value != null) {
				String val = value.toString();
				if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
					sb2.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
				} else if (val.matches(REG_DT)) {
					sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
				} else if (val.matches(REG_TS)) {
					sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
				} else if (value.toString().equals("null")) {
					sb2.append("null");
				} else if (val.contains("'")) {
					sb2.append("'" + value.toString().replaceAll("'", "''") + "'");
				} else if (val.contains("%n")) {
					sb2.append("'" + val.replaceAll("%n", "\n") + "'");
				} else {
					sb2.append("'" + value + "'");
				}
			} else {
				sb2.append("null");
			}
			sb2.append(",");
		}
		return sb1.substring(0, sb1.length() - 1) + ") VALUES (" + sb2.substring(0, sb2.length() - 1) + ")";
	}

	public static String getDeleteSql(String tablename, String condition) {
		StringBuffer sb = new StringBuffer("DELETE FROM ");
		sb.append(tablename);
		if (condition != null) {
			sb.append(" WHERE ");
			sb.append(condition);
		}
		return sb.toString();
	}

	/**
	 * 修改form数据的sql语句
	 * 
	 * @param formStore
	 *            form数据
	 * @param table
	 *            待修改的表名
	 * @param keyField
	 *            条件语句的字段
	 */
	@Deprecated
	public static String getUpdateSqlByFormStore(String formStore, String table, String keyField) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(formStore);
		Set<Object> keys = map.keySet();
		StringBuffer sb1 = new StringBuffer("UPDATE " + table + " SET ");
		Object keyValue = "";
		for (Object key : keys) {
			String field = (String) key;
			Object value = map.get(key);
			if (field.equals(keyField)) {// 找到了需要的字段哦
				keyValue = value;
			}
			sb1.append(field);
			sb1.append("=");
			if (value != null) {
				String val = value.toString();
				if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
					sb1.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
				} else if (val.matches(REG_DT)) {
					sb1.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
				} else if (val.matches(REG_TS)) {
					sb1.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
				} else if (value.toString().equals("null")) {
					sb1.append("null");
				} else if (val.contains("'")) {
					sb1.append("'" + value.toString().replaceAll("'", "''") + "'");
				} else if (val.contains("%n")) {
					sb1.append("'" + val.replaceAll("%n", "\n") + "'");
				} else {
					sb1.append("'" + value + "'");
				}
			} else {
				sb1.append("null");
			}
			sb1.append(",");
		}
		return sb1.substring(0, sb1.length() - 1) + " WHERE " + keyField + "='" + keyValue + "'";
	}

	/**
	 * 生成更新SQL
	 * 
	 * @param map
	 * @param table
	 * @param keyField
	 * @param prefix
	 *            字段前缀
	 * @return
	 */
	public static String getUpdateSqlByFormStore(Map<Object, Object> map, String table, String keyField, String prefix) {
		Set<Object> keys = map.keySet();
		StringBuffer sb1 = new StringBuffer("UPDATE " + table + " SET ");
		Object keyValue = "";
		for (Object key : keys) {
			String field = (String) key;
			if (null != prefix && !field.startsWith(prefix)) {
				continue;
			}
			Object value = map.get(key);
			if (field.equals(keyField)) {// 找到了需要的字段哦
				keyValue = value;
			}
			sb1.append(field);
			sb1.append("=");
			if (value != null) {
				String val = value.toString();
				if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
					sb1.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
				} else if (val.matches(REG_DT)) {
					sb1.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
				} else if (val.matches(REG_TS)) {
					sb1.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
				} else if (val.equals("null")) {
					sb1.append("null");
				} else {
					if (val.contains("'")) {
						val = val.replaceAll("'", "''");
					} else if (val.contains("%n")) {
						val = val.replaceAll("%n", "\n");
					}
					// 针对较长字段，比如clob类型，防止ORA-01704的简单处理：切割成多个字符串连接起来
					if (val.length() > 2000) {
						sb1.append("''");
					} else if (val.length() > 1000) {
						sb1.append(StringUtil.splitAndConcat(val, 666, "'", "'", "||"));
					} else {
						sb1.append("'" + val + "'");
					}
				}
			} else {
				sb1.append("null");
			}
			sb1.append(",");
		}
		return sb1.substring(0, sb1.length() - 1) + " WHERE " + keyField + "='" + keyValue + "'";
	}

	public static String getUpdateSqlByFormStore(Map<Object, Object> map, String table, String keyField) {
		return getUpdateSqlByFormStore(map, table, keyField, null);
	}

	/**
	 * 将HashMap的字段及值，转到SqlMap
	 * 
	 * @param sql
	 * @param map
	 */
	private static void getSqlMap(SqlMap sql, Map<Object, Object> map, boolean replaceQuo) {
		Object value = null;
		for (Object field : map.keySet()) {
			String key = field.toString();
			value = map.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
					sql.setDate(key, val, Constant.YMD);
				} else if (val.matches(REG_DT)) {
					sql.setDate(key, val);
				} else if (val.matches(REG_TS)) {
					sql.setDate(key, val.substring(0, val.lastIndexOf(".")));
				} else if (val.equals("null")) {
					sql.setNull(key);
				} else {
					if(replaceQuo){	
						if (val.contains("'")) {
							val = val.replaceAll("'", "''");
						} else if (val.contains("%n")) {
							val = val.replaceAll("%n", "\n");
						}
					}
					sql.set(key, val);
				}
			} else {
				sql.setNull(key);
			}
		}
	}

	/**
	 * 多类型sql新增操作
	 * 
	 * @param map
	 * @param table
	 * @return
	 */
	/*public static SqlMap getSqlMap(Map<Object, Object> map, String table) {
		SqlMap sql = new SqlMap(table);
		getSqlMap(sql, map);
		return sql;
	}*/
	/**
	 * @param map
	 * @param table
	 * @param replaceQuo
	 * 兼容历史方法，是否掉用sqlmap execute 无需替换单引号
	 * */
	public static SqlMap getSqlMap(Map<Object, Object> map, String table,boolean replaceQuo) {
		SqlMap sql = new SqlMap(table);
		getSqlMap(sql, map, replaceQuo);
		return sql;
	}
	/**
	 * 多类型sql更新操作
	 * 
	 * @param map
	 * @param table
	 * @param keyField
	 * @return
	 */
	/*	public static SqlMap getSqlMap(Map<Object, Object> map, String table, String keyField) {
		SqlMap sql = new SqlMap(table, keyField);
		getSqlMap(sql, map, true);
		return sql;
	}*/
	/**
	 * @param map
	 * @param table
	 * @param keyField
	 * @param replaceQuo
	 * 兼容历史方法，是否掉用sqlmap execute 无需替换单引号
	 * */
	public static SqlMap getSqlMap(Map<Object, Object> map, String table, String keyField, boolean replaceQuo) {
		SqlMap sql = new SqlMap(table, keyField);
		getSqlMap(sql, map, replaceQuo);
		return sql;
	}
	/**
	 * 修改detail数据 将grid数据从字符串形式拼成sql，
	 * 
	 * @param
	 */
	public static List<String> getUpdateSqlbyGridStore(String gridStore, String table, String keyField) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(gridStore);
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> map : maps) {
			Set<Object> keys = map.keySet();
			StringBuffer sb1 = new StringBuffer("UPDATE " + table + " SET ");
			Object keyValue = "";
			for (Object key : keys) {
				String field = (String) key;
				Object value = map.get(key);
				if (field.equals(keyField)) {
					// value不为空，即为已存在数据
					if (value != null && !value.equals("") && !value.equals("null")) {
						keyValue = value;
					} else {// 否则，为新添加的数据
						sb1 = null;
						break;
					}
				}
				sb1.append(field);
				sb1.append("=");
				if (value != null) {
					String val = value.toString();
					if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
						sb1.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
					} else if (val.matches(REG_DT)) {
						sb1.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
					} else if (val.matches(REG_TS)) {
						sb1.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
					} else if (value.toString().equals("null")) {
						sb1.append("null");
					} else if (val.contains("%n")) {
						sb1.append("'" + val.replaceAll("%n", "\n") + "'");
					} else if (val.contains("'")) {
						sb1.append("'" + value.toString().replaceAll("'", "''") + "'");
					} else {
						sb1.append("'" + value + "'");
					}
				} else {
					sb1.append("null");
				}
				sb1.append(",");
			}
			if (sb1 != null)
				sqls.add(sb1.substring(0, sb1.length() - 1) + " WHERE " + keyField + "='" + keyValue + "'");
		}

		return CollectionUtil.reverse(sqls);
	}

	/**
	 * get update_sql by GridStore
	 * 
	 * @param maps
	 * @param table
	 * @param keyField
	 * @return
	 */
	public static List<String> getUpdateSqlbyGridStore(List<Map<Object, Object>> maps, String table, String keyField) {
		List<String> sqls = new ArrayList<String>();
		if (maps != null) {
			for (Map<Object, Object> map : maps) {
				Set<Object> keys = map.keySet();
				StringBuffer sb1 = new StringBuffer("UPDATE " + table + " SET ");
				Object keyValue = "";
				for (Object key : keys) {
					String field = (String) key;
					Object value = map.get(key);
					if (field.equals(keyField)) {
						// value不为空，即为已存在数据
						if (value != null && !value.equals("") && !value.equals("null") && Integer.parseInt(value.toString()) > 0) {
							keyValue = value;
						} else {// 否则，为新添加的数据
							sb1 = null;
							break;
						}
					}
					sb1.append(field);
					sb1.append("=");
					if (value != null) {
						String val = value.toString();
						if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
							sb1.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
						} else if (val.matches(REG_DT)) {
							sb1.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
						} else if (val.matches(REG_TS)) {
							sb1.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
						} else if (value.toString().equals("null")) {
							sb1.append("null");
						} else if (val.contains("%n")) {
							sb1.append("'" + val.replaceAll("%n", "\n") + "'");
						} else if (val.contains("'")) {
							sb1.append("'" + value.toString().replaceAll("'", "''") + "'");
						} else {
							sb1.append("'" + value + "'");
						}
					} else {
						sb1.append("null");
					}
					sb1.append(",");
				}
				if (sb1 != null) {
					sqls.add(sb1.substring(0, sb1.length() - 1) + " WHERE " + keyField + "='" + keyValue + "'");
				}
			}
		}
		return CollectionUtil.reverse(sqls);
	}

	/**
	 * get update_sql by GridStore
	 * 
	 * @param maps
	 * @param table
	 * @param keyField
	 * @return
	 */
	public static List<String> getUpdateSqlbyGridStoreWithoutDate(List<Map<Object, Object>> maps, String table, String keyField) {
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> map : maps) {
			Set<Object> keys = map.keySet();
			StringBuffer sb1 = new StringBuffer("UPDATE " + table + " SET ");
			Object keyValue = "";
			for (Object key : keys) {
				String field = (String) key;
				Object value = map.get(key);
				if (field.equals(keyField)) {
					// value不为空，即为已存在数据
					if (value != null && !value.equals("") && !value.equals("null") && Integer.parseInt(value.toString()) > 0) {
						keyValue = value;
					} else {// 否则，为新添加的数据
						sb1 = null;
						break;
					}
				}
				sb1.append(field);
				sb1.append("=");
				if (value != null) {
					String val = value.toString();
					if (value.toString().equals("null")) {
						sb1.append("null");
					} else if (val.contains("%n")) {
						sb1.append("'" + val.replaceAll("%n", "\n") + "'");
					} else if (val.contains("'")) {
						sb1.append("'" + value.toString().replaceAll("'", "''") + "'");
					} else {
						sb1.append("'" + value + "'");
					}
				} else {
					sb1.append("null");
				}
				sb1.append(",");
			}
			if (sb1 != null) {
				sqls.add(sb1.substring(0, sb1.length() - 1) + " WHERE " + keyField + "='" + keyValue + "'");
			}
		}
		return CollectionUtil.reverse(sqls);
	}

	/**
	 * notice that trigger must be defined
	 * 
	 * @param maps
	 * @param table
	 * @param keyField
	 * @return
	 */
	public static List<String> getInsertOrUpdateSqlbyGridStore(List<Map<Object, Object>> maps, String table, String keyField) {
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> map : maps) {
			if (map.get(keyField) == null || "".equals(map.get(keyField)) || "null".equals(map.get(keyField))
					|| Integer.parseInt(String.valueOf(map.get(keyField))) <= 0) {
				sqls.add(getInsertSql(map, table, keyField));
				continue;
			}
			Set<Object> keys = map.keySet();
			StringBuffer sb1 = new StringBuffer("UPDATE " + table + " SET ");
			Object keyValue = "";
			for (Object key : keys) {
				String field = (String) key;
				Object value = map.get(key);
				if (field.equals(keyField)) {
					// value不为空，即为已存在数据
					if (value != null && !value.equals("") && !value.equals("null") && Integer.parseInt(value.toString()) > 0) {
						keyValue = value;
					} else {// 否则，为新添加的数据
						sb1 = null;
						break;
					}
				}
				sb1.append(field);
				sb1.append("=");
				if (value != null) {
					String val = value.toString();
					if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
						sb1.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
					} else if (val.matches(REG_DT)) {
						sb1.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
					} else if (val.matches(REG_TS)) {
						sb1.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
					} else if (value.toString().equals("null")) {
						sb1.append("null");
					} else if (val.contains("%n")) {
						sb1.append("'" + val.replaceAll("%n", "\n") + "'");
					} else if (val.contains("'")) {
						sb1.append("'" + value.toString().replaceAll("'", "''") + "'");
					} else {
						sb1.append("'" + value + "'");
					}
				} else {
					sb1.append("null");
				}
				sb1.append(",");
			}
			if (sb1 != null)
				sqls.add(sb1.substring(0, sb1.length() - 1) + " WHERE " + keyField + "='" + keyValue + "'");
		}
		return sqls;
	}

	public static List<String> getInsertOrUpdateSqlbyGridStoreWithoutDate(List<Map<Object, Object>> maps, String table, String keyField) {
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> map : maps) {
			if (map.get(keyField) == null || "".equals(map.get(keyField)) || "null".equals(map.get(keyField))
					|| Integer.parseInt(String.valueOf(map.get(keyField))) <= 0) {
				sqls.add(getInsertSql(map, table, keyField));
				continue;
			}
			Set<Object> keys = map.keySet();
			StringBuffer sb1 = new StringBuffer("UPDATE " + table + " SET ");
			Object keyValue = "";
			for (Object key : keys) {
				String field = (String) key;
				Object value = map.get(key);
				if (field.equals(keyField)) {
					// value不为空，即为已存在数据
					if (value != null && !value.equals("") && !value.equals("null") && Integer.parseInt(value.toString()) > 0) {
						keyValue = value;
					} else {// 否则，为新添加的数据
						sb1 = null;
						break;
					}
				}
				sb1.append(field);
				sb1.append("=");
				if (value != null) {
					String val = value.toString();
					if (value.toString().equals("null")) {
						sb1.append("null");
					} else if (val.contains("%n")) {
						sb1.append("'" + val.replaceAll("%n", "\n") + "'");
					} else if (val.contains("'")) {
						sb1.append("'" + value.toString().replaceAll("'", "''") + "'");
					} else {
						sb1.append("'" + value + "'");
					}
				} else {
					sb1.append("null");
				}
				sb1.append(",");
			}
			if (sb1 != null)
				sqls.add(sb1.substring(0, sb1.length() - 1) + " WHERE " + keyField + "='" + keyValue + "'");
		}
		return sqls;
	}

	public static List<String> getInsertOrUpdateSql(List<Map<Object, Object>> maps, String table, String keyField) {
		List<String> sqls = new ArrayList<String>();
		if (maps != null) {
			for (Map<Object, Object> map : maps) {
				if (map.get(keyField) == null || "".equals(map.get(keyField)) || "null".equals(map.get(keyField))
						|| Integer.parseInt(String.valueOf(map.get(keyField))) <= 0) {
					sqls.add(getInsertSql(map, table, keyField));
					continue;
				}
				Set<Object> keys = map.keySet();
				StringBuffer sb1 = new StringBuffer("UPDATE " + table + " SET ");
				Object keyValue = "";
				for (Object key : keys) {
					String field = (String) key;
					Object value = map.get(key);
					if (field.equals(keyField)) {
						// value不为空，即为已存在数据
						if (value != null && !value.equals("") && !value.equals("null") && Integer.parseInt(value.toString()) > 0) {
							keyValue = value;
						} else {// 否则，为新添加的数据
							sb1 = null;
							break;
						}
					}
					sb1.append(field);
					sb1.append("=");
					if (value != null) {
						String val = value.toString();
						if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
							sb1.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
						} else if (val.matches(REG_DT)) {
							sb1.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
						} else if (val.matches(REG_TS)) {
							sb1.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
						} else if (value.toString().equals("null")) {
							sb1.append("null");
						} else if (val.contains("'")) {
							sb1.append("'" + val.replaceAll("'", "''") + "'");
						} else if (val.contains("%n")) {
							sb1.append("'" + val.replaceAll("%n", "\n") + "'");
						} else {
							sb1.append("'" + value + "'");
						}
					} else {
						sb1.append("null");
					}
					sb1.append(",");
				}
				if (sb1 != null)
					sqls.add(sb1.substring(0, sb1.length() - 1) + " WHERE " + keyField + "='" + keyValue + "'");
			}
		}
		return sqls;
	}

	public static Object createOracleLob(OracleConnection conn, String lobClassName) throws Exception {
		@SuppressWarnings("rawtypes")
		Class lobClass = conn.getClass().getClassLoader().loadClass(lobClassName);
		final Integer DURATION_SESSION = new Integer(lobClass.getField("DURATION_SESSION").getInt(null));
		final Integer MODE_READWRITE = new Integer(lobClass.getField("MODE_READWRITE").getInt(null));
		@SuppressWarnings("unchecked")
		Method createTemporary = lobClass.getMethod("createTemporary", new Class[] { Connection.class, boolean.class, int.class });
		Object lob = createTemporary.invoke(null, new Object[] { conn, false, DURATION_SESSION });
		@SuppressWarnings("unchecked")
		Method open = lobClass.getMethod("open", new Class[] { int.class });
		open.invoke(lob, new Object[] { MODE_READWRITE });
		return lob;
	}

	public static String oracleClob2Str(Clob clob) throws Exception {
		return (clob != null ? clob.getSubString(1, (int) clob.length()) : null);
	}

	public static Clob oracleStr2Clob(String str, Clob lob) throws Exception {
		Method methodToInvoke = lob.getClass().getMethod("getCharacterOutputStream", (Class[]) null);
		Writer writer = (Writer) methodToInvoke.invoke(lob, (Object[]) null);
		writer.write(str);
		writer.close();
		return lob;
	}

	public static Map<String, String> splitCondition(String condition, String... fields) {
		if (condition != null && condition.length() > 0) {
			String[] strs = condition.toUpperCase().split(" AND ");
			Map<String, String> ns = new HashMap<String, String>();
			int i = 0;
			int j = 0;
			int len = condition.length();
			for (String s : strs) {
				i = j;
				j += s.length();
				if (i > 0 && j < len) {
					i += 5;
					j += 5;
				}
				if (s.trim().length() > 0) {
					for (String field : fields) {
						if (s.contains(field.toUpperCase()) && s.contains("=")) {
							String[] su = condition.substring(i, j).split("=");
							ns.put(field, su[1]);
							break;
						}
					}
				}
			}
			return ns;
		}
		return null;
	}

	/**
	 * 拆分condition 拆出来的条件还是带单引号的
	 * 
	 * @param condition
	 * @return
	 */
	public static Map<String, String> splitCondition(String condition) {
		if (condition != null && condition.length() > 0) {
			String[] strs = condition.toUpperCase().split(" AND ");
			Map<String, String> ns = new HashMap<String, String>();
			int i = 0;
			int j = 0;
			int len = condition.length();
			for (String s : strs) {
				i = j;
				j += s.length();
				if (i > 0 && j < len) {
					i += 5;
					j += 5;
				}
				if (s.trim().length() > 0) {
					if (s.contains("=")) {
						String[] su = condition.substring(i, j).split("=");
						ns.put(su[0], su[1]);
					}
				}
			}
			return ns;
		}
		return null;
	}

	/**
	 * 字符串按逗号分隔，每个元素加单引号
	 * 
	 * @param str
	 * @return
	 */
	public static String splitToSqlString(String str) {
		return splitToSqlString(str, ",");
	}

	/**
	 * 字符串按逗号分隔，每个元素加单引号
	 * 
	 * @param str
	 * @param separ
	 *            分隔符
	 * @return
	 */
	public static String splitToSqlString(String str, String separ) {
		if (str != null) {
			String[] strs = str.split(separ);
			StringBuffer sb = new StringBuffer();
			for (String k : strs) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append("'").append(k).append("'");
			}
			return sb.toString();
		}
		return null;
	}

	private static boolean isFunction(String field) {
		return field.contains("YM_VIEW_PARAM$");
	}

	private static boolean isDateType(Object data) {
		return null != data && data.toString().matches(Constant.REG_DATE);
	}

	private static String parseFilterField(String field, Object value) {
		if (isDateType(value)) {
			return "trunc(" + field + ",'dd')";
		}
		return field;
	}

	private static Object parseFilterValue(String field, String exp, Object value) {
		if (value instanceof Number) {
			return value;
		} else if (field.contains("YM_VIEW_PARAM$")) {// 调用程序包YM_VIEW_PARAM设置参数的情况
			if (">".equals(exp) || ">=".equals(exp) || "gt".equals(exp) || "gte".equals(exp)) {
				return "ym_view_param.set_from(" + value + ")=" + value;
			} else {
				return "ym_view_param.set_to(" + value + ")=" + value;
			}
		} else {
			if (isDateType(value)) {
				return "to_date('" + value + "','yyyy-mm-dd')";
			}
			return "'" + value + "'";
		}
	}

	/**
	 * 运算符过滤条件
	 * 
	 * @param field
	 * @param queryField
	 * @param opt
	 * @param value
	 * @return
	 */
	private static List<String> parseOperatorFilters(String field, String queryField, String opt, Object value) {
		List<String> filters = new ArrayList<String>();
		if (StringUtil.hasText(value)) {
			boolean isFn = isFunction(field);
			Object parsedValue = parseFilterValue(field, opt, value);
			filters.add((isFn ? "" : (queryField + opt)) + parsedValue);
			// 默认在大小过滤的时候，同时考虑值的长度
			// 比如 201502001 < X < 201502006，如果不对X的长度做限制，会查找出2015020010
			if (!isFn && value instanceof String && !isDateType(value)) {
				// length(ma_code) >= length('201502001')
				String lenOpt = opt.endsWith("=") ? opt : (opt + "=");
				filters.add(String.format("length(%s) %s length(%s)", queryField, lenOpt, parsedValue));
			}
		}
		return filters;
	}

	/**
	 * 将json格式筛选条件转化为oracle sql语句
	 * 
	 * @param filter
	 *            ex: {"pu_kind":"批量采购",
	 *            "pu_date":{"gte":"2017-05-01","lte":"2017-05-31"},
	 *            "pu_currency":["RMB","USD"],"pd_prodcode":{"like":"TL-%"}}
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String parseFilter(Map<String, Object> filter) {
		List<String> filters = new ArrayList<String>();
		Object value = null;
		String queryField = null;
		for (String field : filter.keySet()) {
			value = filter.get(field);
			queryField = parseFilterField(field, value);
			if (null == value) {
				filters.add(queryField + " is null");
			} else if (value instanceof Map) {
				Map<String, Object> mapValue = (Map<String, Object>) value;
				Object val;
				for (String valKey : mapValue.keySet()) {
					val = mapValue.get(valKey);
					queryField = parseFilterField(field, val);
					if (valKey.equals("ne")) {
						if (!StringUtil.hasText(val)) {
							filters.add(queryField + " is not null");
						} else {
							filters.add(queryField + "<>" + parseFilterValue(field, valKey, val));
						}
					} else if (valKey.equals("eq")) {
						if (!StringUtil.hasText(val)) {
							filters.add(queryField + " is null");
						} else {
							filters.add(queryField + "=" + parseFilterValue(field, valKey, val));
						}
					} else if (valKey.equals("gt")) {
						filters.addAll(parseOperatorFilters(field, queryField, ">", val));
					} else if (valKey.equals("lt")) {
						filters.addAll(parseOperatorFilters(field, queryField, "<", val));
					} else if (valKey.equals("gte")) {
						filters.addAll(parseOperatorFilters(field, queryField, ">=", val));
					} else if (valKey.equals("lte")) {
						filters.addAll(parseOperatorFilters(field, queryField, "<=", val));
					} else if (valKey.equals("like")) {
						filters.add(queryField + " like '" + val + "'");
					}
				}
			} else if (value instanceof List) {
				List<Object> orValues = (List<Object>) value;
				List<String> orFilters = new ArrayList<String>();
				for (Object val : orValues) {
					queryField = parseFilterField(field, val);
					orFilters.add(queryField + "=" + parseFilterValue(field, null, val));
				}
				if (orFilters.size() > 0)
					filters.add("(" + CollectionUtil.toString(orFilters, " or ") + ")");
			} else if (value.getClass().isArray()) {
				Object[] orValues = (Object[]) value;
				List<String> orFilters = new ArrayList<String>();
				for (Object val : orValues) {
					queryField = parseFilterField(field, val);
					orFilters.add(queryField + "=" + parseFilterValue(field, null, val));
				}
				if (orFilters.size() > 0)
					filters.add("(" + CollectionUtil.toString(orFilters, " or ") + ")");
			} else {
				if (value.toString().contains("#")) {
					value = value.toString().replaceAll("#", "','");
					filters.add(queryField + " in (" + parseFilterValue(field, null, value) + ")");
				} else {
					filters.add(queryField + "=" + parseFilterValue(field, null, value));
				}
			}
		}
		return CollectionUtil.toString(filters, " and ");
	}
}
