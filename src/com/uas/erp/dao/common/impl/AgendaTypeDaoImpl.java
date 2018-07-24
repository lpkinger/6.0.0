package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.AgendaTypeDao;
import com.uas.erp.model.AgendaType;

@Repository
public class AgendaTypeDaoImpl extends BaseDao implements AgendaTypeDao {

	@Override
	public void delete(int at_id) {
		deleteByCondition("agendatype", "at_id = " + at_id);
	}

	@Override
	public List<AgendaType> getAll(int page, int pageSize) {
		List<AgendaType> ats = new ArrayList<AgendaType>();
		AgendaType at = null;
		String sql = "SELECT at_id,at_name,at_color";
		sql = sql
				+ " FROM("
				+ sql
				+ ",row_number()over(order by at_id desc) rn FROM agendatype ) WHERE rn between "
				+ ((page - 1) * pageSize + 1) + " and " + page * pageSize;
		SqlRowList rs = queryForRowSet(sql);// ma_status=1表示未读
		while (rs.next()) {
			at = new AgendaType();
			at.setAt_id(rs.getInt("at_id"));
			at.setAt_name(rs.getString("at_name"));
			at.setAt_color(rs.getString("at_color"));
			ats.add(at);
		}
		return ats;
	}

	@Override
	public int getAllCount() {
		return getCount("SELECT count(*) FROM agendatype");
	}

	@Override
	public List<AgendaType> getByName(String name, int page, int pageSize) {
		List<AgendaType> ats = new ArrayList<AgendaType>();
		AgendaType at = null;
		String sql = "SELECT at_id,at_name,at_color";
		sql = sql
				+ " FROM("
				+ sql
				+ ",row_number()over(order by at_id desc) rn FROM agendatype WHERE at_name like '%"
				+ name + "%') WHERE rn between " + ((page - 1) * pageSize + 1)
				+ " and " + page * pageSize;
		SqlRowList rs = queryForRowSet(sql);// ma_status=1表示未读
		while (rs.next()) {
			at = new AgendaType();
			at.setAt_id(rs.getInt("at_id"));
			at.setAt_name(rs.getString("at_name"));
			at.setAt_color(rs.getString("at_color"));
			ats.add(at);
		}
		return ats;
	}

	@Override
	public int getSearchCount(String name) {
		return getCountByCondition("agendatype", "at_name like '%" + name + "%'");
	}

	@Override
	public void save(AgendaType at) {
		try {			
			execute("INSERT INTO AGENDATYPE(at_id,at_name,at_color)"
					+ " VALUES(" + at.getAt_id() + "," + at.getAt_name() + ","
					+ at.getAt_color() + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public AgendaType getById(int id) {
		try{
			return getJdbcTemplate().queryForObject("select * from agendaType where at_id=?",
					new BeanPropertyRowMapper<AgendaType>(AgendaType.class), id);			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
