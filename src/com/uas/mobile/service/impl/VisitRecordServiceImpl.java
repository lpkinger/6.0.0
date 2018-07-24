package com.uas.mobile.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.mobile.service.VisitRecordService;

@Service("mobileVisitRecordService")
public class VisitRecordServiceImpl implements VisitRecordService{
	@Autowired
	private BaseDao baseDao;

	@Override
	public String getCustomerCodeNameByNameFuzzy(String name, int size, int page) {
		String sql = "";
		String sql_name = toFuzzyString(name);
		int start = (page - 1) * size + 1;
		int end = page * size;
		sql = "select cu_code, cu_name from (select cu_code, cu_name, rownum rn from "
				+ "customer where cu_name like '" + sql_name + "') where"
				+ "  rn>=" + start + " and rn<=" + end;
		SqlRowList rowList = baseDao.queryForRowSet(sql);
		String result = getJsonForSqlRowList(rowList);
		return result;
	}

	@Override
	public String getCustomerCodeNameByCodeFuzzy(String code, int size, int page) {
		String sql = "";
		String sql_code = toFuzzyString(code.toUpperCase());
		int start = (page - 1) * size + 1;
		int end = page * size;
		sql = "select cu_code, cu_name from (select cu_code, cu_name, rownum rn from "
				+ "customer where  upper(cu_code) like '" + sql_code + "') where"
				+ "  rn>=" + start + " and rn<=" + end;
		SqlRowList rowList = baseDao.queryForRowSet(sql);
		String result = getJsonForSqlRowList(rowList);
		return result;
	}
	
	/**
	 * 把每个查询关键词转换成模糊查询关键词，"科技"->"%科%技%"
	 * @param s
	 * @return
	 */
	public String toFuzzyString(String s) {
		StringBuffer stringBuffer = new StringBuffer(s);
		int length = stringBuffer.length();
		for(int i=length; i >= 0; i --) {
			stringBuffer.insert(i, "%");
		}
		String result = stringBuffer.toString();
		return result;
	}
	
	/**
	 * 为SqlRowList添加获取整个List的JSON字符串的方法
	 * @param rowList
	 * @return
	 */
	public String getJsonForSqlRowList(SqlRowList rowList) {
		String result = "[";
		java.util.Map<String, Object> map = null;
		for(int i=0; i < rowList.getResultList().size(); i++) {
			map = rowList.getAt(i);
			result += "{'cu_code':'" + map.get("cu_code").toString() + "','cu_name':'"
					+ map.get("cu_name").toString() + "'}";
			if(i < rowList.getResultList().size()-1) result += ",";
		}
		result += "]";
		return result;
	}

}
