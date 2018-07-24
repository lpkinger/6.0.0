package com.uas.erp.service.common.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.DebugLog;
import com.uas.erp.service.common.DebugLogService;

@Service
public class DebugLogServiceImpl implements DebugLogService{
	
	@Autowired
	private BaseDao baseDao;

	@Override
	public void save(DebugLog logable) {
		baseDao.save(logable);
	}

	@Override
	public void save(Collection<DebugLog> logables) {
		baseDao.save(logables);
	}
	
}
