package com.uas.erp.dao.common.impl;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.WorkMonthlyDao;
import com.uas.erp.model.WorkMonthly;

@Repository
public class WorkMonthlyDaoImpl extends BaseDao implements WorkMonthlyDao {

	@Override
	public WorkMonthly searchWorkMonthly(int id, String date) {
		try{
			String sql = "select * from workweekly where ww_empid=" + id + " and ww_date=to_date('" + date + "','yyyy-mm-dd')";
			return getJdbcTemplate().queryForObject(sql,
					new BeanPropertyRowMapper<WorkMonthly>(WorkMonthly.class));			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
