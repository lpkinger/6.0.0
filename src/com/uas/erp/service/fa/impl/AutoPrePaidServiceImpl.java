package com.uas.erp.service.fa.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.AutoPrePaidService;

@Service("autoPrePaidService")
public class AutoPrePaidServiceImpl implements AutoPrePaidService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public void autoPrePaid(Integer param) {
		String res = null;
		boolean is;
		is = baseDao.checkByCondition("PrePaid", "to_char(pp_date,'yyyyMM')="
				+ param);
		if (!is) {
			BaseUtil.showError("本月已经有一张摊销单据");
		}
		res = baseDao.callProcedure("SP_CREATEPREPAID", new Object[] { param,
				String.valueOf(SystemSession.getUser().getEm_name()) });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
	}
}
