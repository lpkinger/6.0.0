package com.uas.erp.service.cost;

public interface StepCostService {
	/**
	 * 计算成本阶
	 */
	void countStepCost(Integer param);
	/**
	 * 计算成本
	 */
	void countCost(Integer param);
	/**
	 * 计算产品BOM成本
	 */
	void productCost(Integer param);
	/**
	 * 取期间
	 */
	int getCurrentYearmonth();
}
