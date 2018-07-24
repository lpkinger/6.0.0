package com.uas.erp.service.common.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.common.InitHandService;

@Service
public class InitHandServiceImpl implements InitHandService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public void refreshAP() {
		String yearmonth = baseDao.getFieldValue("Periods", "Pe_Firstday", "pe_code='MONTH-V'", String.class);
		baseDao.callProcedure("SP_REFRESHVENDMONTH_INIT", new Object[] { yearmonth });
	}

	@Override
	public void refreshAR() {
		String yearmonth = baseDao.getFieldValue("Periods", "Pe_Firstday", "pe_code='MONTH-C'", String.class);
		baseDao.callProcedure("SP_REFRESHCUSTMONTH_INIT", new Object[] { yearmonth });
	}

	@Override
	public String refreshLedger() {
		return baseDao.callProcedure("INIT_LEDGER", new Object[] {});
	}

	@Override
	public String refreshJprocessview() {
		return baseDao.callProcedure("SP_REFRESHJPROCESSVIEW_INIT", new Object[] {});
	}

	@Override
	public String refreshOamessagehistoryview() {
		return baseDao.callProcedure("SP_RFOAMESSAGEHISTORYVIEW_INIT", new Object[] {});
	}

}
