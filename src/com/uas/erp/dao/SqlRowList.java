package com.uas.erp.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.bind.Constant;

/**
 * org.springframework.jdbc.support.rowset.SqlRowSet有jdk版本问题
 * 
 * @author yingp 2013-1-10 11:50:08
 */
public class SqlRowList {

	private List<Map<String, Object>> resultList;// 结果集
	private Iterator<Map<String, Object>> iterator;
	private Map<String, Object> currentMap;// 当前Map
	private int currentIndex = -1;// 当前index,从0开始
	private List<String> keys;// 字段名集

	public SqlRowList() {
		this.resultList = new ArrayList<Map<String, Object>>();
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public int size() {
		return getResultList().size();
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public Map<String, Object> getCurrentMap() {
		return currentMap;
	}

	public void setCurrentMap(Map<String, Object> currentMap) {
		this.currentMap = currentMap;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public List<String> getKeys() {
		if (this.keys == null) {
			this.keys = new ArrayList<String>();
			if (this.resultList != null) {
				Iterator<Map<String, Object>> iterator = resultList.iterator();
				if (iterator.hasNext()) {
					Map<String, Object> map = iterator.next();
					Iterator<String> fields = map.keySet().iterator();
					while (fields.hasNext()) {
						this.keys.add(fields.next());
					}
				}
			}
		}
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	/**
	 * 取ResultSet的第index条Map
	 * 
	 * @param index
	 * @return
	 */
	public Map<String, Object> getAt(int index) {
		if (this.resultList != null) {
			Iterator<Map<String, Object>> iterator = resultList.iterator();
			Map<String, Object> map = null;
			int idx = 0;
			while (iterator.hasNext() && (map = iterator.next()) != null) {
				if (index == idx)
					return map;
				idx++;
			}
		}
		return null;
	}

	public <T> List<T> queryForList(String field, Class<T> requiredType) {
		if (this.resultList != null) {
			Iterator<Map<String, Object>> iterator = resultList.iterator();
			Map<String, Object> map = null;
			List<T> list = new ArrayList<T>();
			RowConvert<T> convert = new RowConvert<T>(requiredType);
			field = field.toUpperCase();
			while (iterator.hasNext() && (map = iterator.next()) != null) {
				Object result = map.get(field);
				if (result != null) {
					list.add(convert.convert(result));
				}
			}
			return list;
		}
		return null;
	}

	public <T> Set<T> queryForSet(String field, Class<T> requiredType) {
		if (this.resultList != null) {
			Iterator<Map<String, Object>> iterator = resultList.iterator();
			Map<String, Object> map = null;
			Set<T> set = new java.util.HashSet<T>();
			RowConvert<T> convert = new RowConvert<T>(requiredType);
			field = field.toUpperCase();
			while (iterator.hasNext() && (map = iterator.next()) != null) {
				Object result = map.get(field);
				if (result != null) {
					set.add(convert.convert(result));
				}
			}
			return set;
		}
		return null;
	}

	public boolean hasNext() {
		if (this.resultList != null) {
			if (this.iterator == null) {
				this.iterator = resultList.iterator();
			}
			if (this.iterator.hasNext()) {
				return true;
			}
		}
		return false;
	}

	public boolean next(int count) {
		for (int i = 0; i < count; i++) {
			if (!next())
				return false;
		}
		return true;
	}

	public boolean next() {
		if (this.resultList != null) {
			if (this.iterator == null) {
				this.iterator = resultList.iterator();
				this.currentIndex = -1;
			}
			if (this.iterator.hasNext()) {
				this.currentMap = iterator.next();
				this.currentIndex += 1;
				if (this.keys == null) {
					this.keys = new ArrayList<String>();
					Iterator<String> fields = currentMap.keySet().iterator();
					while (fields.hasNext()) {
						this.keys.add(fields.next());
					}
				}
				return true;
			}
		}
		return false;
	}

	public int getInt(int index) {
		Object obj = getObject(index);
		return obj == null ? -1 : Integer.parseInt(obj.toString());
	}

	public int getInt(String field) {
		Object obj = getObject(field);
		return obj == null ? -1 : Integer.parseInt(obj.toString());
	}

	public int getGeneralInt(int index) {
		Object obj = getObject(index);
		return obj == null ? 0 : Integer.parseInt(obj.toString());
	}

	public int getGeneralInt(String field) {
		Object obj = getObject(field);
		return obj == null ? 0 : Integer.parseInt(obj.toString());
	}

	public float getFloat(int index) {
		Object obj = getObject(index);
		return obj == null ? -1 : Float.parseFloat(obj.toString());
	}

	public float getFloat(String field) {
		Object obj = getObject(field);
		return obj == null ? -1 : Float.parseFloat(obj.toString());
	}

	public float getGeneralFloat(int index) {
		Object obj = getObject(index);
		return obj == null ? 0 : Float.parseFloat(obj.toString());
	}

	public float getGeneralFloat(String field) {
		Object obj = getObject(field);
		return obj == null ? 0 : Float.parseFloat(obj.toString());
	}

	public double getDouble(int index) {
		Object obj = getObject(index);
		return obj == null ? -1 : Double.parseDouble(obj.toString());
	}

	public double getDouble(String field) {
		Object obj = getObject(field);
		return obj == null ? -1 : Double.parseDouble(obj.toString());
	}

	public BigDecimal getBigDecimal(int index) {
		Object obj = getObject(index);
		return obj == null ? null : BigDecimal.valueOf(Double.parseDouble(obj.toString()));
	}

	public BigDecimal getBigDecimal(String field) {
		Object obj = getObject(field);
		return obj == null ? null : BigDecimal.valueOf(Double.parseDouble(obj.toString()));
	}

	public double getGeneralDouble(int index) {
		Object obj = getObject(index);
		return obj == null ? 0 : Double.parseDouble(obj.toString());
	}

	/**
	 * @param index
	 * @param sub
	 *            保留小数位数
	 * @return
	 */
	public double getGeneralDouble(int index, int sub) {
		return NumberUtil.formatDouble(getGeneralDouble(index), sub);
	}

	public double getGeneralDouble(String field) {
		Object obj = getObject(field);
		return obj == null || String.valueOf(obj).trim().equals("") ? 0 : Double.parseDouble(String.valueOf(obj).trim());
	}

	/**
	 * @param field
	 * @param sub
	 *            保留小数位数
	 * @return
	 */
	public double getGeneralDouble(String field, int sub) {
		return NumberUtil.formatDouble(getGeneralDouble(field), sub);
	}

	public BigDecimal getGeneralBigDecimal(int index) {
		Object obj = getObject(index);
		return obj == null ? new BigDecimal(0) : BigDecimal.valueOf(Double.parseDouble(obj.toString()));
	}

	public BigDecimal getGeneralBigDecimal(String field) {
		Object obj = getObject(field);
		return obj == null ? new BigDecimal(0) : BigDecimal.valueOf(Double.parseDouble(obj.toString()));
	}

	public long getLong(int index) {
		Object obj = getObject(index);
		return obj == null ? -1 : Long.parseLong(obj.toString());
	}

	public long getLong(String field) {
		Object obj = getObject(field);
		return obj == null ? -1 : Long.parseLong(obj.toString());
	}

	public long getGeneralLong(int index) {
		Object obj = getObject(index);
		return obj == null ? 0 : Long.parseLong(obj.toString());
	}

	public long getGeneralLong(String field) {
		Object obj = getObject(field);
		return obj == null ? 0 : Long.parseLong(obj.toString());
	}

	public String getString(int index) {
		Object obj = getObject(index);
		return obj == null ? null : obj.toString();
	}

	public String getString(String field) {
		Object obj = getObject(field);
		return obj == null ? null : obj.toString();
	}

	public String getGeneralString(int index) {
		Object obj = getObject(index);
		return obj == null ? "" : obj.toString();
	}

	public String getGeneralString(String field) {
		Object obj = getObject(field);
		return obj == null ? "" : obj.toString();
	}

	public Object getObject(int index) {
		if (this.iterator == null || this.currentMap == null || this.keys == null || index <= 0 || index > this.keys.size()) {
			return null;
		}
		return this.currentMap.get(this.keys.get(index - 1));
	}

	public Object getObject(String field) {
		if (this.iterator == null || this.currentMap == null) {
			return null;
		}
		return this.currentMap.get(field.toUpperCase());
	}

	/**
	 * currentMap的JSON格式
	 */
	public JSONObject getJSONObject() {
		if (this.iterator == null || this.currentMap == null) {
			return null;
		}
		JSONObject json = new JSONObject();
		Object value = null;
		for (String k : this.keys) {
			value = parseValue(this.getObject(k));
			json.put(k, value);
		}
		return json;
	}

	public static Object parseValue(Object value) {
		if (value != null) {
			// 以字符串格式表示时间
			if ("TIMESTAMP".equals(value.getClass().getSimpleName().toUpperCase())) {
				Timestamp time = (Timestamp) value;
				value = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD_HMS);
			}
			// 科学计数法改为js处理
		}
		return value;
	}

	/**
	 * currentMap的JSONString格式
	 */
	public String getJSON() {
		if (this.iterator == null || this.currentMap == null) {
			return null;
		}
		return getJSONObject().toString();
	}

	public Date getDate(int index) {
		Object obj = getObject(index);
		return obj == null ? null : (Date) obj;
	}

	public Date getDate(String field) {
		Object obj = getObject(field);
		return obj == null ? null : (Date) obj;
	}

	public Timestamp getTimestamp(int index) {
		Object obj = getObject(index);
		return obj == null ? null : (Timestamp) obj;
	}

	public Timestamp getTimestamp(String field) {
		Object obj = getObject(field);
		return obj == null ? null : (Timestamp) obj;
	}

	public String getGeneralTimestamp(int index, String format) {
		Timestamp time = getTimestamp(index);
		if (time != null)
			return DateUtil.parseDateToString(new Date(time.getTime()), format);
		return "";
	}

	public String getGeneralTimestamp(int index) {
		return getGeneralTimestamp(index, Constant.YMD_HMS);
	}

	public String getGeneralTimestamp(String field, String format) {
		Timestamp time = getTimestamp(field);
		if (time != null)
			return DateUtil.parseDateToString(new Date(time.getTime()), format);
		return "";
	}

	public String getGeneralTimestamp(String field) {
		return getGeneralTimestamp(field, Constant.YMD_HMS);
	}

	public double getSummary(String field) {
		if (hasNext()) {
			Iterator<Map<String, Object>> iterator = resultList.iterator();
			Map<String, Object> map = null;
			double sum = 0;
			Object obj = null;
			while ((map = iterator.next()) != null) {
				obj = map.get(field.toUpperCase());
				sum += obj == null ? 0 : Double.parseDouble(obj.toString());
			}
			return sum;
		}
		return 0;
	}

	public double getSummary(int index) {
		if (index <= 0 || index > this.getKeys().size()) {
			return 0;
		}
		return getSummary(this.keys.get(index - 1));
	}
}
