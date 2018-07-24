package com.uas.erp.dao.common.impl;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.AgendaDao;
import com.uas.erp.model.Agenda;

@Repository
public class AgendaDaoImpl extends BaseDao implements AgendaDao {

	@Override
	public void delete(int ag_id) {
		try {
			getJdbcTemplate().execute(
					"delete from agenda where ag_id = " + ag_id);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<Agenda> getArrangeList(int em_id, int page, int pageSize) {
		try {
			return getJdbcTemplate("Agenda").query("select * from agenda where ag_arrange_id = ?", 
					new BeanPropertyRowMapper<Agenda>(Agenda.class),em_id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
 
	}

	@Override
	public int getArrangeListCount(int em_id) {
		return getCountByCondition("agenda", "ag_arrange_id = " + em_id);
	}

	@Override
	public List<Agenda> getByCondition(String condition, int page, int pageSize) {
		try {
			return getJdbcTemplate().query("select * from agenda where "+condition, new BeanPropertyRowMapper<Agenda>(Agenda.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int getSearchCount(String condition) {
		return getCountByCondition("agenda", condition);
	}
	@Override
	public Agenda getAgendaById(int id) {
		try{
			return getJdbcTemplate().queryForObject("select * from agenda where ag_id=?",
					new BeanPropertyRowMapper<Agenda>(Agenda.class), id);			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	@Override
	public List<Agenda> getList(int em_id, int page, int pageSize) {
		try {
			return getJdbcTemplate("Agenda").
					query("select * from agenda where ag_arrange_id = ? or ag_executor_id like '%"+em_id+"%'", 
							new BeanPropertyRowMapper<Agenda>(Agenda.class),em_id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public int getListCount(int em_id) {
		return getCountByCondition("agenda", "ag_arrange_id = " + em_id + " or ag_executor like '%" + em_id + "%'");
	}

}
