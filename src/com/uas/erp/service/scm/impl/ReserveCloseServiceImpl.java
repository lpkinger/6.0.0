package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.scm.ReserveCloseService;

@Service("reserveCloseService")
public class ReserveCloseServiceImpl implements ReserveCloseService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private VoucherDao voucherDao;

	@Override
	public void reserveclose(Integer param) {
		String res = baseDao.callProcedure("Sp_EndProduct", new Object[] { param });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.logger.others("结账操作", param + "月库存结账成功", "CheckAccount!IV", "id", param);
	}

	@Override
	public int getCurrentYearmonth() {
		Map<String, Object> map = voucherDao.getJustPeriods("MONTH-P");
		return Integer.parseInt(map.get("PD_DETNO").toString());
	}

	@Override
	public void unperiodsdetail(Integer param) {
		Object first = baseDao.getFieldDataByCondition("periods", "pe_firstday", "pe_code='MONTH-P'");
		if (first != null && param <= Integer.parseInt(first.toString())) {
			BaseUtil.showError("当前期间小于等于初始化期间，不能反结账！");
		}
		String res = baseDao.callProcedure("Sp_UnEndProduct", new Object[] { param });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.logger.others("反结账操作", param + "月库存反结账成功", "CheckAccount!IV", "id", param);
	}
}
