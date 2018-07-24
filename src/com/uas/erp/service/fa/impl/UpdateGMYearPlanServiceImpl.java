package com.uas.erp.service.fa.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.UpdateGMYearPlanService;

@Service
public class UpdateGMYearPlanServiceImpl implements UpdateGMYearPlanService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private VoucherDao voucherDao;

	@Override
	public void UpdateGMYearPlan(int yearmonth) {
		System.out.println(yearmonth);
		String res = baseDao.callProcedure("SP_CALYEARPLAN", new Object[] { yearmonth });
		Employee employee = SystemSession.getUser();	
		if (res.equals("OK")) {
			baseDao.logMessage(new MessageLog(employee.getEm_name(), "刷新年度计划金额", "刷新年度计划金额", "刷新年度计划金额成功"));
		} else {
			BaseUtil.showError(res);
			System.out.println(res);
		}
	}

}
