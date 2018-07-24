package com.uas.erp.service.fa.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.CancelGoodsService;
@Service
public class CancelGoodsServiceImpl implements CancelGoodsService{
	
	@Autowired
	private BaseDao baseDao;

	@Override
	@Transactional
	public void cancelGoods(int yearmonth) {
		String res=baseDao.callProcedure("FA_CANCELGOODSSEND", new Object[]{yearmonth});
		if(!"OK".equals(res)){
			BaseUtil.showError(res);
		}
	}
	@Override
	@Transactional
	public void cancelEstimate(int yearmonth) {
		String res=baseDao.callProcedure("FA_CANCELESTIMATE", new Object[]{yearmonth});
		if(!"OK".equals(res)){
			BaseUtil.showError(res);
		}
	}

}
