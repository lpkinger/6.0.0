package com.uas.erp.service.fa.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.RepAccountService;

@Service("repAccountService")
public class RepAccountServiceImpl implements RepAccountService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public void reportAccount(Integer param) {
		boolean hasUnAccount = baseDao.checkIf("Voucher", "vo_yearmonth=" + param + " AND vo_statuscode<>'ACCOUNT'");
		if (hasUnAccount) {
			BaseUtil.appendError("本月还有未记账的凭证！");
		}
		if (!baseDao.isDBSetting("sys", "allowGLAFarep")) {
			Object yearmonth = baseDao.getFieldDataByCondition("PeriodsDetail", "min(PD_DETNO)", "pd_code='MONTH-A' and pd_status=0");
			if (param < Integer.parseInt(yearmonth.toString())) {
				BaseUtil.showError("总账会计期间[" + param + "]已结账,不能进行报表计算!");
			}
		}
		String res = baseDao.callProcedure("FA_SFS", new Object[] { param });
		if (StringUtil.hasText(res)) {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void wageAccount(Integer param) {
		String res = baseDao.callProcedure("SP_COUNTWAGE", new Object[] { param });
		if (res != null && !res.trim().equals("OK")) {
			BaseUtil.showError(res);
		}
	}
}
