package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.AccountRegisterDetailAss;

public interface AccountRegisterDao {
	Map<String, Object> getJustPeriods(String period);

	Map<String, Object> getPeriodsDate(String periods, Integer date);

	int getPeriodsFromDate(String periods, String date);

	int getPddetno(String periods);

	void validVoucher(int vId);

	int turnPayBalance(int id);

	int turnRecBalance(int id);

	int turnRecBalanceIMRE(int id, String custcode, String thisamount);

	double getTurnAR(Object fp_id);

	/**
	 * 按ID获取银行登记辅助核算
	 * 
	 * @param ar_id
	 * @return
	 */
	List<AccountRegisterDetailAss> getAssByAccountRegisterId(int ar_id);
}
