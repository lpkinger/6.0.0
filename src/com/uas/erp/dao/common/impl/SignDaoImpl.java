package com.uas.erp.dao.common.impl;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.DateUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.SignDao;
import com.uas.erp.model.Sign;

@Repository
public class SignDaoImpl extends BaseDao implements SignDao{
	
	@Override
	public Sign getMySign(String em_code) {
		String date = DateUtil.currentDateString(null);
		try{
			return getJdbcTemplate().queryForObject("select * from sign where si_emcode=? and si_in between to_date('" + 
					date + " 00:00:00','yyyy-MM-dd HH24:mi:ss') and to_date('" + date + " 23:59:59','yyyy-MM-dd HH24:mi:ss')", 
					new BeanPropertyRowMapper<Sign>(Sign.class), em_code);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
}
