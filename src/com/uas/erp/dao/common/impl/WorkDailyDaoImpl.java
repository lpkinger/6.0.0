package com.uas.erp.dao.common.impl;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.WorkDailyDao;
import com.uas.erp.model.WorkDaily;

@Repository
public class WorkDailyDaoImpl extends BaseDao implements WorkDailyDao {

	@Override
	public WorkDaily searchWorkDaily(int id, String date) {
		try{
			String sql = "select * from workdaily where wd_empid=" + id + " and wd_date=to_date('" + date + "','yyyy-mm-dd')";
			return getJdbcTemplate().queryForObject(sql,
					new BeanPropertyRowMapper<WorkDaily>(WorkDaily.class));			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
