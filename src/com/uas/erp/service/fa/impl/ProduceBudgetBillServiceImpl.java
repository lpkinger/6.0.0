package com.uas.erp.service.fa.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.ProduceBudgetBillService;

@Service
public class ProduceBudgetBillServiceImpl implements ProduceBudgetBillService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private VoucherDao voucherDao;

	@Override
	public void ProduceBudgetBill(int yearmonth) {
		String res = baseDao.callProcedure("SP_PRODUCEBUDGET", new Object[] { yearmonth });
		Employee employee = SystemSession.getUser();
		if (res.equals("OK")) {
			baseDao.logMessage(new MessageLog(employee.getEm_name(), "生成收款预算单", "生成收款预算单", "生成收款预算成功"));
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void ProduceFKBudgetBill(int yearmonth) {
		String res = baseDao.callProcedure("SP_PRODUCEFKBUDGET", new Object[] { yearmonth });
		Employee employee = SystemSession.getUser();
		if (res.equals("OK")) {
			baseDao.logMessage(new MessageLog(employee.getEm_name(), "生成付款预算单", "生成付款预算单", "生成付款预算成功"));
		} else {
			BaseUtil.showError(res);
		}
	}
}
