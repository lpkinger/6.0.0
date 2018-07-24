package com.uas.erp.service.cost.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.cost.StepCostService;

@Service("stepCostService")
public class StepCostServiceImpl implements StepCostService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private VoucherDao voucherDao;

	@Override
	public void countStepCost(Integer param) {
		String res = null;
		res = baseDao.callProcedure("sp_cacproductcost", new Object[] { param, SystemSession.getUser().getEm_name() });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void countCost(Integer param) {
		String res = null;
		res = baseDao.callProcedure("SP_CountAllData", new Object[] { param, SystemSession.getUser().getEm_name() });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void productCost(Integer param) {
		String res = null;
		res = baseDao.callProcedure("SP_CACPRODUCTCOST", new Object[] { param, SystemSession.getUser().getEm_name() });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
	}

	@Override
	public int getCurrentYearmonth() {
		Map<String, Object> map = voucherDao.getJustPeriods("MONTH-T");
		return Integer.parseInt(map.get("PD_DETNO").toString());
	}

}
