package com.uas.erp.dao.common.impl;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.WorkWeeklyDao;
import com.uas.erp.model.WorkWeekly;

@Repository
public class WorkWeeklyDaoImpl extends BaseDao implements WorkWeeklyDao {

	@Override
	public WorkWeekly searchWorkWeekly(int id, String date) {
		try{
			String sql = "select * from workweekly where ww_empid=" + id + " and ww_date=to_date('" + date + "','yyyy-mm-dd')";
			return getJdbcTemplate().queryForObject(sql,
					new BeanPropertyRowMapper<WorkWeekly>(WorkWeekly.class));			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
