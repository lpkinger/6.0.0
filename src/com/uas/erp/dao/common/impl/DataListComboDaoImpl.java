package com.uas.erp.dao.common.impl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.model.DataListCombo;

@Repository("dataListComboDao")
public class DataListComboDaoImpl extends BaseDao implements DataListComboDao{
	
	@Override
	@Cacheable(value="combo",key="#caller + #sob + 'getComboxsByCaller'",unless="#result==null")
	public List<DataListCombo> getComboxsByCaller(String caller, String sob) {
		try{
			List<DataListCombo> combos = getJdbcTemplate().query("SELECT * FROM datalistcombo WHERE dlc_caller=? order by dlc_detno", 
					new BeanPropertyRowMapper<DataListCombo>(DataListCombo.class), caller);
			return combos;
		} catch(EmptyResultDataAccessException exception){
			return null;
		}
	}

	@Override
	public List<DataListCombo> getComboxsByCallerAndField(String caller,
			String Field) {
		try{
			List<DataListCombo> combos = getJdbcTemplate().query("SELECT * FROM datalistcombo WHERE upper(dlc_caller)=? AND upper(dlc_fieldname)=? order by nvl(dlc_detno,0)", 
					new BeanPropertyRowMapper<DataListCombo>(DataListCombo.class), caller.toUpperCase(),Field.toUpperCase());
			return combos;
		} catch(EmptyResultDataAccessException exception){
			return null;
		}
	}

	@Override
	public List<DataListCombo> getComboxsByCallers(String callers) {
		try{
			List<DataListCombo> combos = getJdbcTemplate().query("select distinct dlc_fieldname,dlc_display,dlc_value , dlc_value_en,dlc_value_tw from datalistcombo "
					+ "where dlc_display is not null and dlc_value is not null and dlc_caller in("+callers+") order by dlc_fieldname",
					new BeanPropertyRowMapper<DataListCombo>(DataListCombo.class));
			return combos;
		} catch(EmptyResultDataAccessException exception){
			return null;
		}
	}
	
}
