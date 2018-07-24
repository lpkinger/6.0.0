package com.uas.erp.dao.common.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DbfindSetGridDao;
import com.uas.erp.model.DBFindSetGrid;

@Repository("dbfindSetGridDao")
public class DbfindSetGridDaoImpl extends BaseDao implements DbfindSetGridDao{

	@Override
	public List<DBFindSetGrid> getDbFindSetGridsByCaller(String caller) {
		return getJdbcTemplate().query("SELECT * from dbfindsetgrid where ds_caller=? order by ds_detno", 
				new BeanPropertyRowMapper<DBFindSetGrid>(DBFindSetGrid.class), caller);
	}
	@Override
	@CacheEvict(value="dbfind",allEntries=true)
	public void deleteDbFindSetGridById(int id) {
		execute("delete from dbfindsetgrid where ds_id=?", id);
	}
	
}
