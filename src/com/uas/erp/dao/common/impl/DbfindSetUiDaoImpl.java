package com.uas.erp.dao.common.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DbfindSetUiDao;
import com.uas.erp.model.DBFindSetUI;

@Repository("dbfindSetUiDao")
public class DbfindSetUiDaoImpl extends BaseDao implements DbfindSetUiDao {

	@Override
	@Cacheable(value = "dbfindsetui", key = "#sob + '@' + #caller + #field + 'getDbFindSetUIByField'", unless = "#result==null")
	public DBFindSetUI getDbFindSetUIByField(String caller, String field, String sob) {
		int count = getCount("select count(*) from dbfindsetui where ds_whichui='" + field + "'");
		if (count > 1) {
			try {
				DBFindSetUI dbFindSetUI = getJdbcTemplate().queryForObject("SELECT * from dbfindsetui WHERE ds_caller=? AND ds_whichui=?",
						new BeanPropertyRowMapper<DBFindSetUI>(DBFindSetUI.class), caller, field);
				return dbFindSetUI;
			} catch (EmptyResultDataAccessException e) {
				try {
					DBFindSetUI dbFindSetUI = getJdbcTemplate().queryForObject(
							"SELECT * from dbfindsetui WHERE ds_whichui=? AND ds_caller is null",
							new BeanPropertyRowMapper<DBFindSetUI>(DBFindSetUI.class), field);
					return dbFindSetUI;
				} catch (EmptyResultDataAccessException em) {
					BaseUtil.showError("【caller:" + caller + "】,字段【" + field + "】配置有误!");
					return null;
				}
			} catch (Exception e) {
				return null;
			}
		} else if (count == 0) {
			BaseUtil.showError("【caller:" + caller + "】,字段【" + field + "】未配置dbFind!");
			return null;
		} else {
			DBFindSetUI dbFindSetUI = getJdbcTemplate().queryForObject("SELECT * from dbfindsetui WHERE ds_whichui=?",
					new BeanPropertyRowMapper<DBFindSetUI>(DBFindSetUI.class), field);
			return dbFindSetUI;
		}
	}

	@Override
	public DBFindSetUI getDbFindSetUIById(int id) {
		DBFindSetUI dbFindSetUI = getJdbcTemplate().queryForObject("SELECT * from dbfindsetui WHERE ds_id=?",
				new BeanPropertyRowMapper<DBFindSetUI>(DBFindSetUI.class), id);
		return dbFindSetUI;
	}

	@Override
	public void deleteDbFindSetUIById(int id) {
		execute("delete * from dbfindSetUI where ds_id=?", id);
	}

	@Override
	public DBFindSetUI getDbFindSetUIByCallerAndField(String caller, String field) {
		try {
			DBFindSetUI dbFindSetUI = getJdbcTemplate().queryForObject("SELECT * from dbfindsetui WHERE ds_caller=? AND ds_whichui=?",
					new BeanPropertyRowMapper<DBFindSetUI>(DBFindSetUI.class), caller, field);
			return dbFindSetUI;
		} catch (Exception e) {
			return null;
		}
	}
}
