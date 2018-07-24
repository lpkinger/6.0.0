package com.uas.erp.dao.common.impl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.ButtonDao;
import com.uas.erp.model.GridButton;

@Repository
public class ButtonDaoImpl extends BaseDao implements ButtonDao {

	@Override
	@Cacheable(value = "gridbutton",unless="#result==null")
	public List<GridButton> getGridButtons(String sob, String caller) {
		try {
			return getJdbcTemplate().query("SELECT * from gridbutton where gb_caller=? order by gb_id asc",
					new BeanPropertyRowMapper<GridButton>(GridButton.class), caller);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}
