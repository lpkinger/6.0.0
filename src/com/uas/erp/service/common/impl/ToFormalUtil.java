package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.InitData;
import com.uas.erp.model.InitDetail;
import com.uas.erp.model.InitToFormal;

/**
 * 初始化数据转入正式
 * 
 * @author yingp
 */
public class ToFormalUtil {

	public static final String KEY_FIELD = "keyField";
	public static final String CODE_FIELD = "codeField";
	public static final String DATE_FIELD = "date";
	public static final String NUMBER_FIELD = "number";
	public static final String REPLACE_FIELD = "replace";
	public static final String COPY_FIELD_IF = "copyif";
	public static final String COPY_FIELD_OF = "copyof";
	public static final String SESSION_EMID = "em_id";
	public static final String SESSION_EMCODE = "em_code";
	public static final String SESSION_EMNAME = "em_name";

	public final static String REG_YMD = "\\d{4}-\\d{1,2}-\\d{1,2}";
	public final static String REG_YMD_HIS = "\\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2}:\\d{2}";
	public final static String REG_YMD_HIS_T = "\\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2}:\\d{2}.\\d{1}";

	public final static String REG_YMD_SPRIT = "\\d{4}/\\d{1,2}/\\d{1,2}";
	public final static String REG_MDY_SPRIT = "\\d{1,2}/\\d{1,2}/\\d{2,4}";

	public final static String ymdFormat = "yyyy-mm-dd";
	public final static String ymdhisFormat = "yyyy-mm-dd hh24:mi:ss";
	public final static String ymdspritFormat = "yyyy/mm/dd";
	public final static String dmyspritFormat = "dd/mm/yyyy";

	private List<InitDetail> inits;
	private List<InitData> datas;
	private List<InitToFormal> formals;
	private int count = 0;
	private JSONObject currentData;
	// 只取一次sequence,之后依次++
	private Map<String, Integer> keys;
	private Map<String, Object[]> codes;
	private Map<String, String> sql_f;
	private StringBuffer sql_v;
	private Map<String, String> date_orcl;
	private Map<String, Map<String, String>> d_replace;
	private Employee employee;

	private BaseDao baseDao;

	public ToFormalUtil() {

	}

	public List<InitToFormal> getFormals() {
		return formals;
	}

	public ToFormalUtil(List<InitDetail> inits, List<InitData> datas, Employee employee) {
		this();
		this.inits = inits;
		this.datas = datas;
		this.count = datas.size();
		this.keys = new HashMap<String, Integer>();
		this.codes = new HashMap<String, Object[]>();
		this.sql_f = new HashMap<String, String>();
		this.date_orcl = new HashMap<String, String>();
		this.d_replace = new HashMap<String, Map<String, String>>();
		this.formals = new ArrayList<InitToFormal>();
		this.baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		this.employee = employee;
		toFormal();
	}

	public synchronized void toFormal() {
		String table = null;
		String field = null;
		Object val = null;
		String value = null;
		String def = null;
		int idid = 0;
		boolean ifTab = false;
		int j = 0;
		StringBuffer sql = new StringBuffer();
		for (InitData d : this.datas) {
			currentData = JSONObject.fromObject(d.getId_data());
			idid = d.getId_id();
			sql_v = new StringBuffer("VALUES (");
			j = 0;
			for (InitDetail t : this.inits) {
				table = t.getId_table().toLowerCase();
				field = t.getId_field();
				val = currentData.get(field);
				value = val == null ? null : val.toString();
				def = t.getId_default();
				if (!sql_f.containsKey(table)) {
					ifTab = true;
					if (j == 0) {
						sql.append("INSERT /*+Append*/ INTO ");
						sql.append(table);
						sql.append("(");
						j++;
					}
					sql.append(field);
					sql.append(",");
				}
				parseDefault(table, field, value, def, t.getId_type());
			}
			if (ifTab) {
				sql_f.put(table, sql.substring(0, sql.length() - 1) + ") ");
				ifTab = false;
			}
			formals.add(new InitToFormal(idid, sql_f.get(table) + sql_v.substring(0, sql_v.length() - 1) + ")"));
		}
	}

	private void parseDefault(String table, String field, String value, String def, String type) {
		Object val = value;
		if (def != null) {
			if (def.startsWith(KEY_FIELD)) {
				val = getSeq(table, field);
				currentData.put(field, val);
				sql_v.append(val);
			} else if (def.startsWith(CODE_FIELD)) {
				sql_v.append("'");
				val = getCode(table);
				sql_v.append(val);
				sql_v.append("'");
			} else if (def.startsWith(DATE_FIELD)) {
				if(def.contains("yy") && !def.contains("yyyy"))def=def.replace("yy", "yyyy");
				val = getDate(def);
				sql_v.append(val);
			} else if (def.startsWith(REPLACE_FIELD)) {
				if (value != null && value.length()!= 0 && !value.trim().isEmpty()) {
					sql_v.append("'");
					val = getReplace(field, def, value);
					sql_v.append(val);
					sql_v.append("'");
				} else {
					sql_v.append("null");
				}
			} else if (def.startsWith(COPY_FIELD_IF)) {
				if (value == null || value.length() == 0) {
					val = getCopy(def);
					currentData.put(field, val);
				}
				sql_v.append("'");
				sql_v.append(val);
				sql_v.append("'");
			} else if (def.startsWith(COPY_FIELD_OF)) {
				val = getCopy(def);
				currentData.put(field, val);
				sql_v.append("'");
				sql_v.append(val);
				sql_v.append("'");
			} else if (def.startsWith(SESSION_EMID)) {
				val = employee.getEm_id();
				currentData.put(field, val);
				sql_v.append("'");
				sql_v.append(val);
				sql_v.append("'");
			} else if (def.startsWith(SESSION_EMCODE)) {
				val = employee.getEm_code();
				currentData.put(field, val);
				sql_v.append("'");
				sql_v.append(val);
				sql_v.append("'");
			} else if (def.startsWith(SESSION_EMNAME)) {
				val = employee.getEm_name();
				currentData.put(field, val);
				sql_v.append("'");
				sql_v.append(val);
				sql_v.append("'");
			} else {
				sql_v.append("'");
				sql_v.append(def);
				sql_v.append("'");
			}
		} else {
			if (value != null && value.trim().length() > 0) {
				if (type.startsWith("date")) {
					if (value.matches(REG_YMD)) {
						value = "to_date('" + value + "','" + ymdFormat + "')";
					} else if (value.matches(REG_YMD_HIS)) {
						value = "to_date('" + value + "','" + ymdhisFormat + "')";
					} else if (value.matches(REG_YMD_HIS_T)) {
						value = "to_date('" + value.substring(0, value.lastIndexOf(".")) + "','" + ymdhisFormat
								+ "')";
					} else if (value.matches(REG_YMD_SPRIT)) {
						value = "to_date('" + value + "','" + ymdspritFormat + "')";
					} else {
						value = "sysdate";
					}
					sql_v.append(value);
				} else if (type.startsWith("number")) {
					sql_v.append("'");
					sql_v.append(value.replaceAll(",", ""));
					sql_v.append("'");
				} else {
					sql_v.append("'");
					sql_v.append(value.replaceAll("'", "''"));// 考虑oracle sql单引号
					sql_v.append("'");
				}
			} else {
				sql_v.append("null");
			}
		}
		sql_v.append(",");
	}

	/**
	 * 根据配置的日期格式，获取日期
	 */
	private String getDate(String def) {
		if (date_orcl.containsKey(def)) {
			return date_orcl.get(def);
		} else {
			def = def.substring(def.indexOf("(") + 1, def.lastIndexOf(")"));
			String val = null;
			try {
				val = DateUtil.parseDateToOracleString(def, new Date());
			} catch (Exception e) {
				val = DateUtil.parseDateToOracleString(null, new Date());
			}
			date_orcl.put(def, val);
			return val;
		}
	}

	/**
	 * 将数据替换成数据库实际值
	 */
	private String getReplace(String field, String def, String value) {
		String val = value;
		if (d_replace.containsKey(field)) {
			val = d_replace.get(field).get(value);
			if (val == null) {
				val = value;
			}
		} else {
			def = def.substring(def.indexOf("(") + 1, def.lastIndexOf(")"));
			String[] arr = def.split(",");
			String[] rs;
			Map<String, String> com = new HashMap<String, String>();
			for (String r : arr) {
				rs = r.split(":");
				com.put(rs[0], rs[1]);
				if (rs[0].equals(value)) {
					val = rs[1];
				}
			}
			d_replace.put(field, com);
		}
		return val;
	}

	/**
	 * 复制其它字段的数据
	 */
	private String getCopy(String def) {
		def = def.substring(def.indexOf("(") + 1, def.lastIndexOf(")"));
		return String.valueOf(currentData.get(def));
	}

	/**
	 * 一次取count个序列号
	 */
	public synchronized int getSeq(String tabName, String field) {
		int id = 0;
		if (keys.containsKey(tabName)) {
			id = keys.get(tabName) + 1;
		} else {
			String seq = tabName.toUpperCase() + "_SEQ";
			id = baseDao.getSeqId(seq);
			Object maxId = baseDao.getFieldDataByCondition(tabName, "max(" + field + ")", "1=1");
			if (maxId != null) {
				if (Integer.parseInt(maxId.toString()) > id) {
					baseDao.execute("alter sequence " + seq + " increment by "
							+ (Integer.parseInt(maxId.toString()) - id + 1));
					baseDao.getSeqId(seq);
					baseDao.execute("alter sequence " + seq + " increment by 1");
					id = baseDao.getSeqId(seq);
				}
			}
			if (count > 1) {
				baseDao.execute("alter sequence " + seq + " increment by " + (count - 1));
				baseDao.getSeqId(seq);
				baseDao.execute("alter sequence " + seq + " increment by 1");
			}
		}
		keys.put(tabName, id);
		return id;
	}

	/**
	 * 一次取多个流水号
	 */
	public String getCode(String tabName) {
		String code = null;
		if (codes.containsKey(tabName)) {
			Object[] objs = codes.get(tabName);
			if (objs[0] == null) {
				int num = Integer.parseInt(objs[1].toString()) + 1;
				codes.put(tabName, new Object[] { objs[0], num });
				code = String.valueOf(num);
			} else {
				int num = Integer.parseInt(objs[1].toString()) + 1;
				codes.put(tabName, new Object[] { objs[0], num });
				code = objs[0] + String.valueOf(num).substring(2);
			}
		} else {
			code = baseDao.sGetMaxNumber(tabName, 2);			
			Object[] objs = baseDao.getFieldsDataByCondition("maxnumbers", "mn_leadcode,mn_number",
					"upper(mn_tablename)='" + tabName.toUpperCase() + "'");
			codes.put(tabName, objs);
			if (count > 1) {
				baseDao.execute("UPDATE maxnumbers set mn_number=mn_number+? where upper(mn_tablename)=?", count,
						tabName.toUpperCase());
			}
		}
		return code;
	}
}
