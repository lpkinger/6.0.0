package com.uas.erp.service.pm.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.MakeMonthService;

@Service
public class MakeMonthServiceImpl implements MakeMonthService{
	
	@Autowired
	private BaseDao baseDao;

	@Override
	public void RefreshMakeMonthNew(String currentMonth, String caller) {
		String str = baseDao.callProcedure("Sp_gRefreshMakeMonth", new Object[] { currentMonth });
		if (str != null && !str.equals("")) {
			BaseUtil.showError(str);
		}
	}

}
