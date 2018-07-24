package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

public interface MonthAccountService {
	/**
	 * 应收期末对账
	 * 
	 * @param condition
	 * @return
	 */
	List<Map<String, Object>> getArAccount(String condition);

	/**
	 * 应付期末对账
	 * 
	 * @param condition
	 * @return
	 */
	List<Map<String, Object>> getApAccount(String condition);

	/**
	 * 固定资产期末对账
	 * 
	 * @param chkun
	 *            包含未记账凭证
	 * @return
	 */
	List<Map<String, Object>> getFixAccount(boolean chkun);

	/**
	 * 累计折旧期末对账
	 * 
	 * @param chkun
	 *            包含未记账凭证
	 * @return
	 */
	List<Map<String, Object>> getDepreAccount(boolean chkun);

	/**
	 * 银行期末对账
	 * 
	 * @param chkun
	 *            包含未记账凭证
	 * @return
	 */
	List<Map<String, Object>> getBankAccount(boolean chkun);

	/**
	 * 应收票据期末对账
	 * 
	 * @param chkun
	 *            包含未记账凭证
	 * @return
	 */
	List<Map<String, Object>> getBillArAccount(boolean chkun);

	/**
	 * 应付票据期末对账
	 * 
	 * @param chkun
	 *            包含未记账凭证
	 * @return
	 */
	List<Map<String, Object>> getBillApAccount(boolean chkun);

	void startAccount(int yearmonth, String module, String caller);

	void overAccount(int yearmonth, String module, String caller);

	void startAccount(int yearmonth);

	void overAccount(int yearmonth);

	void startAccountAP(int yearmonth);

	void overAccountAP(int yearmonth);

	void startAccountGL(int yearmonth);

	void overAccountGL(int yearmonth);

	void startAccount(Integer param);

	void overAccount(Integer param);

	/**
	 * 预登帐
	 */
	void preWriteVoucher();

	void startAccountPLM(int yearmonth);

	void overAccountPLM(int yearmonth);

	void getShareRate(int yearmonth);

	void createShareVoucher(int yearmonth);

	void refreshEndData(String mould);
}
