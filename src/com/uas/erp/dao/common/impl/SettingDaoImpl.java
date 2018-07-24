package com.uas.erp.dao.common.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.SettingDao;

@Repository("SettingDao")
public class SettingDaoImpl extends BaseDao implements SettingDao {

	@Override
	@Cacheable(value = "default", key = "'getInOutTypes'")
	public Map<String, String> getInOutTypes() {
		SqlRowList rs = queryForRowSet("select ds_table,ds_inorout from documentsetup order by ds_table");
		Map<String, String> ios = new HashMap<String, String>();
		while (rs.next()) {
			ios.put(rs.getString(1), rs.getString(2));
		}
		return ios;
	}

}
