package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataDao;
import com.uas.erp.model.DataColumn;
import com.uas.erp.model.DataIndex;

@Repository
public class DataDaoImpl extends BaseDao implements DataDao {

	private static final String CSQL1 = "SELECT count(*) " + "FROM user_tables u left join datadictionary d "
			+ "on u.table_name = d.dd_tablename " + "where d.dd_tablename is null";

	private static final String CSQL2 = "SELECT count(*) " + "FROM user_tables u right join datadictionary d "
			+ "on u.table_name = d.dd_tablename " + "where u.table_name is null";

	private static final String CSQL3 = "SELECT count(*) " + "FROM datadictionarydetail d full join ("
			+ "SELECT t1.table_name,t2.column_name " + "FROM user_tables t1 left join user_tab_columns t2 "
			+ "on t1.table_name=t2.table_name) t " + "on d.ddd_tablename=t.table_name and d.ddd_fieldname=lower(t.column_name) "
			+ "where t.table_name is null";

	private static final String CSQL4 = "SELECT count(*) " + "FROM datadictionarydetail d full join ("
			+ "SELECT t1.table_name,t2.column_name " + "FROM user_tables t1 left join user_tab_columns t2 "
			+ "on t1.table_name=t2.table_name) t " + "on d.ddd_tablename=t.table_name and d.ddd_fieldname=lower(t.column_name) "
			+ "where d.ddd_tablename is null";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<DataColumn> insertDD() {
		String sql = "SELECT u.table_name FROM user_tables u " + "left join datadictionary d " + "on u.table_name = d.dd_tablename "
				+ "where d.dd_tablename is null " + "order by u.table_name";
		List<DataColumn> list = getJdbcTemplate().query(sql, new BeanPropertyRowMapper(DataColumn.class));
		sql = "INSERT INTO DATADICTIONARY(DD_TABLENAME) ";
		for (DataColumn dc : list) {
			sql += "SELECT '" + dc.getDd_tablename() + "' from dual union all ";
		}
		sql.substring(0, sql.lastIndexOf("union all"));
		return list;
	}

	@Override
	public List<DataColumn> createTables() {
		String sql = "SELECT d.dd_tablename FROM user_tables u " + "full join datadictionary d " + "on u.table_name = d.dd_tablename "
				+ "where u.table_name is null " + "order by d.dd_tablename";
		List<DataColumn> list = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<DataColumn>(DataColumn.class));
		return list;
	}

	@Override
	public List<DataColumn> insertDDD() {
		String sql = "SELECT t.table_name,t.column_name,t.data_type,t.data_length,d.ddd_tablename,d.ddd_fieldname "
				+ "FROM datadictionarydetail d full join (SELECT t1.table_name,t2.column_name,t2.data_type,t2.data_length "
				+ "FROM user_tables t1 left join user_tab_columns t2 " + "on t1.table_name=t2.table_name) t "
				+ "on d.ddd_tablename=t.table_name and d.ddd_fieldname=lower(t.column_name) " + "where d.ddd_tablename is null "
				+ "order by t.table_name";
		List<DataColumn> list = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<DataColumn>(DataColumn.class));
		// sql =
		// "INSERT INTO DATADICTIONARYDETAIL(DDD_DDID,DDD_TABLENAME,DDD_FIELDNAME) ";
		// for (DataColumn dc : list) {
		// sql += "SELECT " + getDDID(dc.getTable_name()) + ",'" +
		// dc.getTable_name()+ "','" + dc.getColumn_name().toLowerCase() +
		// "' from dual union all ";
		// }
		// sql.substring(0, sql.lastIndexOf("union all"));
		return list;
	}

	@Override
	public List<DataColumn> alterTable() {
		String sql = "SELECT t.table_name,t.column_name,d.ddd_tablename,d.ddd_fieldname,d.ddd_fieldtype "
				+ "FROM datadictionarydetail d full join (SELECT t1.table_name,t2.column_name "
				+ "FROM user_tables t1 left join user_tab_columns t2 " + "on t1.table_name=t2.table_name) t "
				+ "on d.ddd_tablename=t.table_name and d.ddd_fieldname=lower(t.column_name) " + "where t.table_name is null "
				+ "order by d.ddd_tablename";
		List<DataColumn> list = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<DataColumn>(DataColumn.class));
		// for (DataColumn dc : list) {
		// sql = "alter table " + dc.getDdd_tablename() + " add " +
		// dc.getDdd_fieldname() + " " + dc.getDdd_fieldtype();
		// }
		return list;
	}

	@Override
	public List<DataColumn> eqType() {
		String sql = "SELECT t.table_name,t.column_name,t.data_type,t.data_length,d.ddd_tablename,d.ddd_fieldname,d.ddd_fieldtype "
				+ "FROM datadictionarydetail d full join (SELECT t1.table_name,t2.column_name,t2.data_type,t2.data_length "
				+ "FROM user_tables t1 left join user_tab_columns t2 " + "on t1.table_name=t2.table_name) t "
				+ "on d.ddd_tablename=t.table_name and d.ddd_fieldname=lower(t.column_name) " + "order by t.table_name,t.column_name";
		List<DataColumn> list = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<DataColumn>(DataColumn.class));
		return list;
	}

	public int getDDID(String tablename) {
		String sql = "select dd_id from datadictionary where dd_tablename = '" + tablename + "'";
		SqlRowList rs = queryForRowSet(sql);
		if (rs.next()) {
			return rs.getInt(1);
		}
		return 0;
	}

	@Override
	public List<DataColumn> getDetailByTablename(String tablename) {
		String sql = "select a.TABLE_NAME,a.COLUMN_NAME,a.DATA_LENGTH,a.DATA_TYPE,a.NULLABLE,a.DATA_DEFAULT,a.DATA_PRECISION,B.Comments from user_tab_columns  "
				+ "a left join User_Col_Comments  b on a.table_name=B.Table_Name and A.Column_Name=B.Column_Name  where a.table_name= upper('"
				+ tablename + "') order by column_id";
		List<DataColumn> list = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<DataColumn>(DataColumn.class));
		return list;
	}

	@Override
	public List<DataColumn> getPropertyByTablename(String tablename) {
		String sql = "select a.TABLE_NAME,a.COLUMN_NAME,a.DATA_TYPE,B.Comments,C.allowbatchupdate_ from user_tab_columns  a left join User_Col_Comments  b "
				+ "on a.table_name=B.Table_Name and A.Column_Name=B.Column_Name left join Tab_Col_property C on a.table_name=C.TableName_ and A.Column_Name=C.Colname_  "
				+ "where a.table_name= upper('" + tablename + "') order by column_id";
		List<DataColumn> list = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<DataColumn>(DataColumn.class));
		return list;
	}

	@Override
	public void createTables(String[] tablenames) {
		for (String tn : tablenames) {
			String sql = "select ddd_tablename,ddd_fieldname,ddd_fieldtype,ddd_allowblank,ddd_defaultvalue "
					+ "from datadictionarydetail where ddd_tablename='" + tn + "'";
			List<DataColumn> columns = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<DataColumn>(DataColumn.class));
			String sql2 = "CREATE TABLE " + tn + "(";
			for (int i = 0; i < columns.size(); i++) {
				DataColumn dc = columns.get(i);
				sql2 += dc.getDdd_fieldname() + " " + dc.getDdd_fieldtype();
				if ("no".equalsIgnoreCase(dc.getDdd_allowblank()) || "n".equalsIgnoreCase(dc.getDdd_allowblank())
						|| "f".equalsIgnoreCase(dc.getDdd_allowblank())) {
					sql2 += " NOT NULL ";
				}
				if (dc.getDdd_defaultvalue() != null && dc.getDdd_defaultvalue() != "") {
					sql2 += " DEFAULT '" + dc.getDdd_defaultvalue() + "'";
				}
				if (i == columns.size() - 1) {
					sql2 += ")";
				} else {
					sql2 += ",";
				}
			}
			execute(sql2);

		}

	}

	@Override
	public List<DataColumn> test(int start, int limit) {
		String sql = "select * from( " + "select rownum rn, t.* from( "
				+ "SELECT u.table_name FROM user_tables u left join datadictionary d " + "on u.table_name = d.dd_tablename "
				+ "where d.dd_tablename is null " + "order by u.table_name) t) where rn >" + start + " and rn <= " + (limit + start);
		List<DataColumn> list = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<DataColumn>(DataColumn.class));
		return list;
	}

	@Override
	public int getTotal() {
		String sql = "SELECT count(*) FROM user_tables u left join datadictionary d "
				+ "on u.table_name = d.dd_tablename where d.dd_tablename is null";
		SqlRowList rs = queryForRowSet(sql);
		if (rs.next()) {
			return rs.getInt(1);
		}
		return 0;
	}

	/**
	 * 获取详细信息 得到数据字典中缺省的用户表信息 i=1 得到数据字典中存在而用户并没有创建的所有表 i=2
	 * 得到数据字典中存在的表字段，而用户表结构缺省的字段信息 i=3 获取数据字典中缺省的用户表的相关字段信息 i=4
	 */
	@Override
	public List<DataColumn> add(int start, int limit, int i) {
		String sql = "";
		if (i == 1) {
			sql = "select * from( " + "select rownum rn, t.* from( " + "SELECT u.table_name FROM user_tables u left join datadictionary d "
					+ "on u.table_name = d.dd_tablename " + "where d.dd_tablename is null " + "order by u.table_name) t) where rn >"
					+ start + " and rn <= " + (limit + start);
		} else if (i == 2) {
			sql = "SELECT * FROM (" + "select rownum rn, t.* from(" + "SELECT d.dd_tablename "
					+ "FROM user_tables u full join datadictionary d " + "on u.table_name = d.dd_tablename "
					+ "where u.table_name is null " + "order by d.dd_tablename) t) where rn >" + start + " and rn <= " + (limit + start);
		} else if (i == 3) {
			sql = "SELECT * FROM (" + "SELECT rownum rn,ta.* FROM (" + "SELECT d.ddd_tablename,d.ddd_fieldname,d.ddd_fieldtype "
					+ "FROM datadictionarydetail d full join (" + "SELECT t1.table_name,t2.column_name "
					+ "FROM user_tables t1 left join user_tab_columns t2 " + "on t1.table_name=t2.table_name) t "
					+ "on d.ddd_tablename=t.table_name and lower(d.ddd_fieldname)=lower(t.column_name) " + "where t.table_name is null "
					+ "order by d.ddd_tablename,d.ddd_fieldname) ta) " + "where rn >" + start + " and rn <= " + (limit + start);
		} else if (i == 4) {
			sql = "SELECT * FROM (" + "SELECT rownum rn,ta.* FROM (" + "SELECT t.table_name,t.column_name,t.data_type,t.data_length "
					+ "FROM datadictionarydetail d full join (" + "SELECT t1.table_name,t2.column_name,t2.data_type,t2.data_length "
					+ "FROM user_tables t1 left join user_tab_columns t2 " + "on t1.table_name=t2.table_name) t "
					+ "on d.ddd_tablename=t.table_name and lower(d.ddd_fieldname)=lower(t.column_name) " + "where d.ddd_tablename is null "
					+ "order by t.table_name,t.column_name) ta) " + "where rn >" + start + " and rn <= " + (limit + start);
		} else {
			return null;
		}
		List<DataColumn> list = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<DataColumn>(DataColumn.class));
		return list;
	}

	/**
	 * 获取对应信息记录条数 得到数据字典中缺省的用户表信息 i=1 得到数据字典中存在而用户并没有创建的所有表 i=2
	 * 得到数据字典中存在的表字段，而用户表结构缺省的字段信息 i=3 获取数据字典中缺省的用户表的相关字段信息 i=4
	 */
	@Override
	public int getTotal(int i) {
		String sql = "";
		if (i == 1) {
			sql = DataDaoImpl.CSQL1;
		} else if (i == 2) {
			sql = DataDaoImpl.CSQL2;
		} else if (i == 3) {
			sql = DataDaoImpl.CSQL3;
		} else if (i == 4) {
			sql = DataDaoImpl.CSQL4;
		} else {
			return 0;
		}
		SqlRowList rs = queryForRowSet(sql);
		if (rs.next()) {
			return rs.getInt(1);
		}
		return 0;
	}

	@Override
	public String addFields(List<Map<Object, Object>> maps) {
		String sql = "begin ";
		String error = "";
		for (Map<Object, Object> map : maps) {
			int ddid = getDDID(map.get("table_name").toString());
			if (ddid == 0) {
				error += "[" + map.get("table_name") + "] ";
			} else {
				sql += "insert into "
						+ map.get("table_name").toString()
						+ "(ddd_ddid,ddd_depno,ddd_tablename,ddd_fieldname,ddd_fieldtype) values("
						+ ddid
						+ ", "
						+ (getDepno(map.get("table_name").toString()) + 1)
						+ ", '"
						+ map.get("table_name").toString()
						+ "', '"
						+ map.get("column_name").toString().toLowerCase()
						+ "', '"
						+ (map.get("data_type").toString().equals("VARCHAR2") ? map.get("data_type").toString() + "("
								+ map.get("data_length").toString() + ")" : map.get("data_type").toString()) + "');";
			}
		}
		sql += " end;";
		// execute(sql);
		return error;
	}

	public int getDepno(String tablename) {
		String sql = "SELECT max(ddd_detno) FROM datadictionarydetail where ddd_tablename = '" + tablename.toUpperCase() + "'";
		SqlRowList rs = queryForRowSet(sql);
		if (rs.next()) {
			return rs.getInt(1) == -1 ? 0 : rs.getInt(1);
		}
		return 0;
	}

	@Override
	public void alter(List<Map<Object, Object>> maps) {
		// String sql = "begin ";
		// for(Map<Object, Object> map:maps){
		// sql += "alter table " + map.get("ddd_tablename").toString() + " add "
		// + map.get("ddd_fieldname").toString() + " " +
		// map.get("ddd_fieldtype").toString() + ";";
		// }
		// sql += " end;";
		// execute(sql);
	}

	@Override
	public List<DataIndex> getColumnIndexByTablename(String tablename) {
		// TODO Auto-generated method stub
		String SQL = "SELECT INDEX_NAME,UNIQUENESS,TABLE_NAME  FROM USER_INDEXES WHERE TABLE_NAME=?";
		String COLSQL = "SELECT COLUMN_NAME,DESCEND FROM  USER_IND_COLUMNS WHERE INDEX_NAME=? AND TABLE_NAME=?";
		List<DataIndex> lists = getJdbcTemplate().query(SQL, new BeanPropertyRowMapper<DataIndex>(DataIndex.class),
				new Object[] { tablename });
		SqlRowList sl = null;
		List<Map<String, Object>> maps = null;
		for (DataIndex ind : lists) {
			sl = queryForRowSet(COLSQL, new Object[] { ind.getIndex_name(), tablename });
			maps = new ArrayList<Map<String, Object>>();
			while (sl.next()) {
				maps.add(sl.getCurrentMap());
			}
			ind.setInd_columns(maps);
		}
		return lists;
	}

}
