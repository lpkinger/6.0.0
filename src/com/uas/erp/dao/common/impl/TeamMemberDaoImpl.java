package com.uas.erp.dao.common.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.TeamMemberDao;
import com.uas.erp.model.Teammember;
@Repository
public class TeamMemberDaoImpl extends BaseDao implements TeamMemberDao {

	@Override
	public Teammember getTeammemberByIdCode(int team_id, String employee_code) {
		try{
			return getJdbcTemplate().queryForObject("select * from TEAMMEMBER where tm_teamid=" + team_id + " and tm_employeecode='" + employee_code + "'",
					new BeanPropertyRowMapper<Teammember>(Teammember.class));			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
