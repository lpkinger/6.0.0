package com.uas.erp.service.b2c.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.b2c.B2CGetDataService;


@Service("b2CGetDataService")
public class B2CGetDataServiceImpl implements B2CGetDataService {
	
	@Autowired
	private BaseDao baseDao;
	@Override
	public int getDatalistCount(String caller, String condition, String table, String fields) {
		// TODO Auto-generated method stub
		int cn = 0;
		if(condition==null || condition.equals("")){
			SqlRowList rs = baseDao.queryForRowSet("select count(1) cn from "+table);
			if(rs.next()){
				cn = rs.getInt("cn");
			}
		}else{
			SqlRowList rs = baseDao.queryForRowSet("select count(1) cn from "+table+" where "+condition);
			if(rs.next()){
				cn = rs.getInt("cn");
			}
		}
		return cn;
	}

	@Override
	public String getDatalistData(String caller, String condition, String table, String fields, String orderby, int page, int pageSize) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		String[] field = fields.split(",");
		String sql = getSql(fields, table, condition, orderby, page, pageSize);
		SqlRowList rs = baseDao.queryForRowSet(sql);
		while(rs.next()){
			Map<String, Object> map = new HashMap<String, Object>();
			for(int i = 0; i<field.length; i++){
				if (fields.contains(" ")) {// column有取别名
					String[] strs = field[i].split(" ");
					field[i] = strs[strs.length - 1];
				}
				Object value = rs.getObject(field[i]);
				value = value == null || value.equals("null") ? "" : SqlRowList.parseValue(value);
				map.put(field[i], value);
			}
			data.add(map);
		}
		return BaseUtil.parseGridStore2Str(data);
	}
	
	private String getSql(String fields, String table, String condition, String orderby, int page, int pageSize){

		StringBuffer aliasStr = new StringBuffer();
		StringBuffer fieldStr = new StringBuffer();
		fieldStr.append(fields);
		condition = "".equals(condition) ? "" : " WHERE " + condition;
		orderby = orderby == null ? " " : orderby;
		StringBuffer aliasSql = new StringBuffer("select tab.*");
		aliasSql.append(aliasStr).append(" from (select * from ").append(table).append(") tab");
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		StringBuffer sb = new StringBuffer("select * from (select TT.*, ROWNUM rn from (select ");
		sb.append(fieldStr);
		sb.append(" from ");
		sb.append("(").append(aliasSql).append(")");
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

	@Override
	public List<Map<String, Object>> getFieldsDatas(String caller, String fields, String condition, String tableName) {
		// TODO Auto-generated method stub
		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append(fields);
		sql.append(" FROM ");
		sql.append(tableName);
		sql.append(" WHERE ");
		sql.append(condition);
		SqlRowList rs = baseDao.queryForRowSet(sql.toString());
		if(rs.next()){
			List<Map<String, Object>> list = rs.getResultList();
			Map<String, Object> map = new HashMap<String, Object>();
			List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
			Iterator<Map<String, Object>> iter = list.iterator();
			while (iter.hasNext()) {
				Map<String, Object> map1 = new HashMap<String, Object>();
				map = iter.next();
				Iterator<String> it = map.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					Object value = map.get(key);
					if(value instanceof Date){
						value = "'"+map.get(key)+"'";
					}
					map1.put(key.toLowerCase(), value);
				}
				datas.add(map1);
		     }
			return datas;
		}
		return null;
	}

}
