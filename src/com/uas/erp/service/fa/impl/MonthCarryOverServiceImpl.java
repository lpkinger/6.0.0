package com.uas.erp.service.fa.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.fa.MonthCarryOverService;

/**
 * fa模块中固定资产月底结账操作
 * 
 * @author madan
 */
@Service("monthCarryOverService")
public class MonthCarryOverServiceImpl implements MonthCarryOverService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private VoucherDao voucherDao;

	@Override
	public void carryover(Integer param) {
		String res = baseDao.callProcedure("SP_ENDASSETS", new Object[] { param });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.logger.others("结账操作", param + "月固定资产结账成功", "CheckAccount!FIX", "id", param);
	}

	@Override
	@Transactional
	public void rescarryover(Integer param) {
		Object first = baseDao.getFieldDataByCondition("periods", "pe_firstday", "pe_code='MONTH-F'");
		if (first != null && param <= Integer.parseInt(first.toString())) {
			BaseUtil.showError("当前期间小于等于初始化期间，不能反结账！");
		}
		String res = baseDao.callProcedure("SP_UNENDASSETS", new Object[] { param });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.logger.others("反结账操作", param + "月固定资产反结账成功", "CheckAccount!FIX", "id", param);
	}
}
