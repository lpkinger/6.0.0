package com.uas.erp.service.scm.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ChargeBackService;
@Service
public class ChargeBackServiceImpl implements ChargeBackService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public void calChargeBack(Integer param) {
		if (baseDao.checkIf("ChargeBack", "CB_YEARMONTH=" + param )) {
			BaseUtil.showError("当前期间已经产生了借机扣款表，不能重复计算！");
		}
		int count=baseDao.getCount("select count(1) from ChargeBack where CB_YEARMONTH>"+param);
		if(count>0){
			BaseUtil.showError("已经存在大于当前期间的借机扣款表，不能计算！");
		}
		String res = baseDao.callProcedure("SP_CHARGEBACK", new Object[] { param });
		if (StringUtil.hasText(res)) {
			BaseUtil.showError(res);
		}
	}
}
