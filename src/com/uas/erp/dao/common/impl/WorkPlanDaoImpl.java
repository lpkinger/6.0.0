package com.uas.erp.dao.common.impl;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.WorkPlanDao;
import com.uas.erp.model.WorkPlan;
import com.uas.erp.model.WorkPlanDetail;

@Repository
public class WorkPlanDaoImpl extends BaseDao implements WorkPlanDao {

	@Override
	public WorkPlan getWorkPlanById(int wp_id) {
		try{
			return getJdbcTemplate().queryForObject("select * from workplan where wp_id=?",
					new BeanPropertyRowMapper<WorkPlan>(WorkPlan.class), wp_id);			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	@Override
	public WorkPlan getWorkPlanByTitle(String title) {
		try{
			return getJdbcTemplate().queryForObject("select * from workplan where wp_title=?",
					new BeanPropertyRowMapper<WorkPlan>(WorkPlan.class), title);			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<WorkPlanDetail> getWorkPlanDetailList(int wpd_wpid) {
		try {
			return getJdbcTemplate().query("select * from workplandetail where wpd_wpid=" + wpd_wpid + " order by wpd_id asc", 
					new BeanPropertyRowMapper<WorkPlanDetail>(WorkPlanDetail.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
