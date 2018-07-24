package com.uas.erp.dao;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.util.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;

/**
 * 封装sql操作，提供对特殊类型的值的灵活处理
 * 
 * @author yingp
 * 
 * @since 2015-11-26
 *        <p>
 *        增加更新功能，参考构造函数；增加对lob数据的处理
 *        </p>
 * 
 */
public class SqlMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7665440776209042811L;

	/**
	 * 如果sysdate,getdate()等符合该正则表达式的字符串不需要特殊处理<br>
	 * 就调用set(String, Object, boolean)方法,第三个参数为false
	 */
	private static final String REG_SPECIAL = "(?:sysdate|getdate\\(\\))";

	private static final int longStringBytes = 4000;// 字符串最长字节数，超过了就得使用lob类型

	private String table;

	/**
	 * 主键
	 */
	private String primaryKey;
	/**
	 * 主键值
	 */
	private Object primaryValue;

	private boolean isNew = true;

	private List<String> fields;

	private Object[] values;

	private Set<String> specialKeys;

	public SqlMap() {
		fields = new ArrayList<String>();
		values = new Object[] {};
		specialKeys = new HashSet<String>();
	}

	/**
	 * 新增模式
	 * 
	 * @param table
	 *            待插入的表
	 */
	public SqlMap(String table) {
		this();
		this.table = table;
	}

	/**
	 * 修改模式
	 * 
	 * @param table
	 *            待插入的表
	 * @param primaryKey
	 *            主键
	 */
	public SqlMap(String table, String primaryKey) {
		this(table);
		this.primaryKey = primaryKey;
		isNew = false;
	}

	/**
	 * 修改模式
	 * 
	 * @param table
	 *            待插入的表
	 * @param primaryKey
	 *            主键
	 * @param primaryValue
	 *            主键值
	 */
	public SqlMap(String table, String primaryKey, Object primaryValue) {
		this(table);
		this.primaryKey = primaryKey;
		this.primaryValue = primaryValue;
		isNew = false;
	}

	/**
	 * 添加要插入的字段
	 * 
	 * @param field
	 *            toSql字段
	 * @param value
	 *            字段的值
	 * @param type
	 *            字段的类型
	 */
	public void set(String field, Object value) {
		if (!isNew && field.equals(primaryKey)) {
			primaryValue = value;
		} else {
			if (isSpecial(value)) {
				setSpecial(field, value);
			} else {
				if (value != null) {
					if (value.toString().matches(Constant.REG_DATE)) {
						setDate(field, value.toString(), Constant.YMD);
					} else if (value.toString().matches(Constant.REG_DATETIME)) {
						setDate(field, value.toString(), Constant.YMD_HMS);
					} else {
						setObject(field, value);
					}
				} else {
					setObject(field, value);
				}
			}
		}
	}

	public void setObject(String field, Object value) {
		if (fields.contains(field)) {
			int index = fields.indexOf(field);
			values[index] = value;
		} else {
			fields.add(field);
			values = Arrays.copyOf(values, values.length + 1);
			values[values.length - 1] = value;
		}
	}

	/**
	 * 添加要插入的字段
	 * 
	 * @param field
	 *            toSql字段
	 * @param value
	 *            字段的值
	 * @param special
	 *            是否是特殊字符,比如调用数据库函数，参数等
	 */
	public void set(String field, Object value, boolean special) {
		if (!isNew && field.equals(primaryKey)) {
			primaryValue = value;
		} else {
			if (special) {
				setSpecial(field, value);
			} else {
				if (value != null) {
					if (value.toString().matches(Constant.REG_DATE)) {
						setDate(field, value.toString(), Constant.YMD);
					} else if (value.toString().matches(Constant.REG_DATETIME)) {
						setDate(field, value.toString(), Constant.YMD_HMS);
					} else {
						setObject(field, value);
					}
				} else {
					setObject(field, value);
				}
			}
		}
	}

	/**
	 * 添加要插入的字段
	 * 
	 * @param field
	 *            toSql字段
	 * @param value
	 *            字段的值
	 */
	public void setSpecial(String field, Object value) {
		if (!isNew && field.equals(primaryKey)) {
			primaryValue = value;
		} else {
			setObject(field, value);
			specialKeys.add(field);
		}
	}

	/**
	 * 添加要插入的字段 <b>字段值为空</b>
	 * 
	 * @param field
	 *            toSql字段
	 * @param value
	 *            字段的值
	 */
	public void setNull(String field) {
		setSpecial(field, "null");
	}

	/**
	 * 添加要插入的字段<b>Double型</b>
	 * 
	 * @param field
	 *            toSql字段
	 * @param value
	 *            {Double}字段的值
	 */
	public void setDouble(String field, Double value) {
		value = value == null ? 0 : value;
		setSpecial(field, value);
	}

	/**
	 * 添加要插入的字段<b>日期型</b>
	 * 
	 * @param to
	 *            toSql字段
	 * @param value
	 *            {Date}字段的值
	 */
	public void setDate(String field, Date value) {
		setDate(field, value, Constant.YMD_HMS);
	}

	/**
	 * 添加要插入的字段<b>日期型</b>
	 * 
	 * @param to
	 *            toSql字段
	 * @param value
	 *            {Date}字段的值
	 * @param format
	 *            格式
	 */
	public void setDate(String field, Date value, String format) {
		if (value == null)
			setNull(field);
		else
			setSpecial(field, DateUtil.parseDateToOracleString(format, value));
	}

	/**
	 * 添加要插入的字段<b>日期型</b>
	 * 
	 * @param to
	 *            toSql字段
	 * @param value
	 *            {Date}字段的值
	 */
	public void setDate(String field, String value) {
		setDate(field, value, Constant.YMD_HMS);
	}

	/**
	 * 添加要插入的字段<b>日期型</b>
	 * 
	 * @param to
	 *            toSql字段
	 * @param value
	 *            {Date}字段的值
	 * @param format
	 *            格式
	 */
	public void setDate(String field, String value, String format) {
		if (value == null)
			setNull(field);
		else
			setSpecial(field, DateUtil.parseDateToOracleString(format, value));
	}

	private static boolean isSpecial(Object value) {
		if (value != null) {
			return value.toString().matches(REG_SPECIAL);
		}
		return false;
	}

	/**
	 * 是否lob类型
	 * 
	 * @param value
	 * @return
	 */
	private static boolean isLob(Object value) {
		return value != null && value.toString().getBytes().length > longStringBytes;
	}

	/**
	 * insert语句
	 * 
	 * @param mark
	 *            {true: value以参数形式传入}
	 * @return
	 */
	public String getInsertSql(boolean mark) {
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO ").append(table).append("(").append(BaseUtil.parseList2Str(fields, ",", true)).append(") VALUES (")
				.append(getEnd(mark)).append(")");
		return sb.toString();
	}

	/**
	 * update语句
	 * 
	 * @param mark
	 *            {true: value以参数形式传入}
	 * @return
	 */
	public String getUpdateSql(boolean mark) {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE ").append(table).append(" SET ").append(getEnd(mark));
		return sb.toString();
	}

	/**
	 * 行复制语句
	 * 
	 * @param mark
	 *            {true: value以参数形式传入}
	 * @return
	 */
	public String getCopySql(String condition) {
		StringBuffer sb = new StringBuffer("begin ");
		sb.append("for rs in (select * from ");
		sb.append(table);
		if (!StringUtils.isEmpty(condition))
			sb.append(" where ").append(condition);
		else if (primaryValue != null)
			sb.append(" where ").append(primaryKey).append("=").append(primaryValue);
		sb.append(") loop");
		int i = 0;
		for (String f : fields) {
			sb.append(" rs.");
			sb.append(f);
			sb.append(":=");
			if (specialKeys.contains(f)) {
				sb.append(values[i++]);
			} else {
				sb.append("'").append(values[i++]).append("'");
			}
			sb.append(";");
		}
		sb.append(" insert into ");
		sb.append(table);
		sb.append(" values rs;");
		sb.append(" end loop; ");
		sb.append(" end;");
		return sb.toString();
	}

	/**
	 * insertOrUpdate语句
	 * 
	 * @param mark
	 *            {true: value以参数形式传入}
	 * @return
	 */
	public String getSql(boolean mark) {
		return isNew ? getInsertSql(mark) : getUpdateSql(mark);
	}

	/**
	 * 执行sql
	 */
	public void execute() {
		BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		baseDao.getJdbcTemplate().update(getSql(true), values);
	}

	/**
	 * 执行sql
	 */
	public void executeCopy(String condition) {
		BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		baseDao.getJdbcTemplate().update(getCopySql(condition));
	}

	/**
	 * 执行sql
	 * 
	 * @param jdbcTemplate
	 */
	public void execute(JdbcTemplate jdbcTemplate, LobHandler lobHandler) {
		jdbcTemplate.execute(getSql(true), new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
			@Override
			protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException, DataAccessException {				
				/*int j=1;
				for (int i=0;i<values.length-1 ;i++) {
					if(!specialKeys.contains(fields.get(i))){
						if (isLob(values[i]))
							lob.setClobAsString(ps, j, values[i].toString());
						else
							ps.setObject(j, values[i]);
						j++;
					}		
				}
				ps.setObject(j, primaryValue);*/
				int i = 0;
				for (Object value : values) {
					i++;
					if (isLob(value))
						lob.setClobAsString(ps, i, value.toString());
					else
						ps.setObject(i, value);
				}
			}
		});
	}	
	/**
	 * @param mark
	 *            {true: value以参数形式传入}
	 * @return
	 */
	private String getEnd(boolean mark) {
		int len = fields.size();
		StringBuffer sb = new StringBuffer();
		String field = null;
		Object[] tmp = new Object[values.length -specialKeys.size()];
        int k=0;
		for (int i = 0; i < len; i++) {
			if (sb.length() > 0)
				sb.append(",");
			field = fields.get(i);
			if (!isNew)
				sb.append(field).append("=");
			if (specialKeys.contains(field)) {
				sb.append(values[i]);				
			} else {
				tmp[k]=values[i];
				k++;
				if (mark)
					sb.append("?");
				else
					sb.append("'").append(values[i]).append("'");
			}
		}
		if (!isNew) {
			sb.append(" WHERE ").append(primaryKey).append("=");
			if (mark) {
				sb.append("?");
				tmp=Arrays.copyOf(tmp, tmp.length+1);
				tmp[tmp.length-1]=primaryValue;
			} else {
				sb.append("'").append(primaryValue).append("'");
			}
		}
		values=Arrays.copyOf(tmp, tmp.length);
		return sb.toString();
	}

}
