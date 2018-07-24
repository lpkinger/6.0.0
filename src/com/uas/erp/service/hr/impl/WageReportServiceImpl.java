package com.uas.erp.service.hr.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.WageReportService;

@Service
public class WageReportServiceImpl implements WageReportService {
	@Autowired
	private BaseDao baseDao;
	
	@Override
	public void calculate(String date) {
		String res = null;
		res = baseDao.callProcedure("SP_WAGEREPORTCAlC", new Object[] { date, SystemSession.getUser().getEm_name() });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void delete(String date) {
		baseDao.deleteByCondition("wagereport", "wr_date='"+date+"'");
	}

}
