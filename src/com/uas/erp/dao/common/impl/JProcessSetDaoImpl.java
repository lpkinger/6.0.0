package com.uas.erp.dao.common.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.JProcessSetDao;
import com.uas.erp.model.JProcessSet;

@Repository("processSetDao")
public class JProcessSetDaoImpl extends BaseDao implements JProcessSetDao {
	
	@Override
	public JProcessSet getCallerInfo(String caller) {
		final String sql = "select * from JProcessSet where js_caller = ?";
	     JProcessSet js=null;
	   
		try {
			js = getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<JProcessSet>(JProcessSet.class),new Object[]{caller});
			return js;
		} catch (Exception e) {
			throw new RuntimeException("请在 ‘JProcessSet’表里配置流程相关信息！");
			
		}
	   
	}

}
