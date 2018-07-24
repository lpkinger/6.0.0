package com.uas.erp.service.cost.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.cost.MonthCarryOverService;

/**
 * cost模块中成本月底结账作业
 * 
 * @author madan
 */
@Service("costMonthCarryOverService")
public class MonthCarryOverServiceImpl implements MonthCarryOverService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private VoucherDao voucherDao;

	@Override
	public void carryover(String caller, Integer param) {
		String res = null;
		res = baseDao.callProcedure("Sp_EndCost", new Object[] { param });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		baseDao.logger.others("结账操作", param + "月成本结账成功", "CheckAccount!COST", "id", param);
	}

	@Override
	public void rescarryover(String caller, Integer param) {
		Object first = baseDao.getFieldDataByCondition("periods", "pe_firstday", "pe_code='MONTH-T'");
		if (first != null && param <= Integer.parseInt(first.toString())) {
			BaseUtil.showError("当前期间小于等于初始化期间，不能反结账！");
		}
		String res = baseDao.callProcedure("Sp_UnEndCost", new Object[] { param });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		baseDao.logger.others("反结账操作", param + "月成本反结账成功", "CheckAccount!COST", "id", param);
	}

	@Override
	public int getCurrentYearmonth() {
		Map<String, Object> map = voucherDao.getJustPeriods("MONTH-T");
		return Integer.parseInt(map.get("PD_DETNO").toString());
	}
}
