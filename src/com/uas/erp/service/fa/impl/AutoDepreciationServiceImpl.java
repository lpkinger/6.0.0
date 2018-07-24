package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.fa.AutoDepreciationService;

@Service("autoDepreciationService")
public class AutoDepreciationServiceImpl implements AutoDepreciationService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private VoucherDao voucherDao;

	@Override
	public void accrued(Integer param) {
		String res = null;
		boolean is;
		is = baseDao.checkByCondition("AssetsDepreciation", "to_char(de_date,'yyyyMM')=" + param + " and de_class='折旧单'");
		if (!is) {
			BaseUtil.showError("本月已经有一张折旧单");
		}
		res = baseDao.callProcedure("Sp_CountAssets", new Object[] { param, String.valueOf(SystemSession.getUser().getEm_name()) });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
	}

	@Override
	public int getCurrentYearmonth() {
		Map<String, Object> map = voucherDao.getJustPeriods("MONTH-F");
		return Integer.parseInt(map.get("PD_DETNO").toString());
	}

	@Override
	public int getCurrentYearmonthAR() {
		Map<String, Object> map = voucherDao.getJustPeriods("MONTH-C");
		return Integer.parseInt(map.get("PD_DETNO").toString());
	}

	@Override
	public int getCurrentYearmonthAP() {
		Map<String, Object> map = voucherDao.getJustPeriods("MONTH-V");
		return Integer.parseInt(map.get("PD_DETNO").toString());
	}

	@Override
	public int getCurrentYearmonthGL() {
		Map<String, Object> map = voucherDao.getJustPeriods("MONTH-A");
		return Integer.parseInt(map.get("PD_DETNO").toString());
	}

	@Override
	public int getCurrentYearmonthPLM() {
		Map<String, Object> map = voucherDao.getJustPeriods("MONTH-O");
		return Integer.parseInt(map.get("PD_DETNO").toString());
	}

	@Override
	public int getCurrentYearmonthGS() {
		Map<String, Object> map = voucherDao.getJustPeriods("MONTH-B");
		return Integer.parseInt(map.get("PD_DETNO").toString());
	}
}
