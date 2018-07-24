package com.uas.erp.dao.common.impl;

import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DbfindSetDao;
import com.uas.erp.model.DBFindSet;
import com.uas.erp.model.DBFindSetDetail;

@Repository("dbfindSetDao")
public class DbfindSetDaoImpl extends BaseDao implements DbfindSetDao{

	@Override
	@Cacheable(value="dbfind",key="#sob + '@' + #caller + 'getDbfind'",unless="#result==null")
	public DBFindSet getDbfind(String caller, String sob) {
		try {
			DBFindSet dbFindSet = getJdbcTemplate().queryForObject("select * from dbfindset where ds_caller=?", 
					new BeanPropertyRowMapper<DBFindSet>(DBFindSet.class),caller);
			List<DBFindSetDetail> dbFindSetDetails = getJdbcTemplate(dbFindSet.getDs_tablename()).query(
					"select * from dbfindsetdetail where dd_dsid=? order by dd_ddno", 
					new BeanPropertyRowMapper<DBFindSetDetail>(DBFindSetDetail.class),dbFindSet.getDs_id());
			dbFindSet.setDbFindSetDetails(dbFindSetDetails);
			return dbFindSet;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
