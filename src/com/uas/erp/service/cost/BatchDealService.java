package com.uas.erp.service.cost;

import com.uas.erp.model.Employee;

public interface BatchDealService {
	/**
	 * 存货核算
	 * 
	 * @param language
	 * @param employee
	 * @param caller
	 * @param data
	 * @param condition
	 *            查询条件
	 * @param condParams
	 *            按条件执行时，作为额外条件传回的字段
	 */
	void accountProdio(String language, Employee employee, String caller, String data, String condition, String condParams);

	/**
	 * 取价
	 * 
	 * @param language
	 * @param employee
	 * @param caller
	 * @param data
	 * @param condition
	 *            查询条件
	 * @param condParams
	 *            按条件执行时，作为额外条件传回的字段
	 */
	void getPrice(String language, Employee employee, String caller, String data, String condition, String condParams);

	void shareFee(String language, Employee employee, String caller, String data);

	void resPrice(String language, Employee employee, String caller, String data);

	void batchSave(String language, Employee employee, String caller, String data);

	void consistency(Integer param, String language, Employee employee);

	void consistencySale(Integer param, String language, Employee employee);

	void vastSaveCostDetail(String language, Employee employee, String caller, String data);

	void vastNowhVoucherCredit(String caller, String data);

	void vastDifferVoucherCredit(String caller, String data);

	void vastSaveCostDetailMaterial(String language, Employee employee, String caller, String data);
}
