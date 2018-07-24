package com.uas.erp.service.ma;

public interface InitGuideService {

	/**
	 * 校验基本配置表，和基本数据表
	 * 
	 * @return
	 */
	String checkBaseTables();

	/**
	 * 导入前校验是否必须资料已导入
	 * 
	 * @return
	 */
	String checkBefore(String tables);

	String checkTab(String table);

	/**
	 * 修补基础配置、数据缺漏项
	 * 
	 * 来源数据为uaserp标准配置帐套
	 * 
	 * @param tbs
	 * @return
	 */
	void repairTabs(String tbs);
	
	int getCount(String table, String condition);
}