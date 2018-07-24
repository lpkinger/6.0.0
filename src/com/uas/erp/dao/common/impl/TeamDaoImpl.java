package com.uas.erp.dao.common.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.TeamDao;
import com.uas.erp.model.Team;

@Repository
public class TeamDaoImpl extends BaseDao implements TeamDao {

	@Override
	public Team getTeamByCode(String code) {
		try{
			return getJdbcTemplate().queryForObject("select * from TEAM where team_code=?",
					new BeanPropertyRowMapper<Team>(Team.class), code);			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
