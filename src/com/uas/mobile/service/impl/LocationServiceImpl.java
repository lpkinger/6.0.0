package com.uas.mobile.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.mobile.service.LocationService;

@Service("locationService")
public class LocationServiceImpl implements LocationService{
	@Autowired
	private BaseDao baseDao;

	@Override
	public void saveLocation(String data,String currentMaster) {
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(data);
	    baseDao.execute(SqlUtil.getInsertSqlbyGridStore(maps, currentMaster+".EMPLOYEETRACK"));
	}
}
