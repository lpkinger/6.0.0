package com.uas.mobile.service;

/**
 * 
 * @author suntg
 * @date 2014年10月25日10:46:25
 * 移动版客户拜访报告service层接口
 *
 */
public interface VisitRecordService {

	/**
	 * 通过客户名称模糊查询获取客户编号和客户名称
	 * @param name 客户名称的查询条件
	 * @return 返回结果JSON字符串
	 */
	public String getCustomerCodeNameByNameFuzzy(String name, int size, int page);
	
	/**
	 * 通过客户编号模糊查询获取客户编号和客户名称
	 * @param name 客户编号的查询条件
	 * @return 返回结果JSON字符串
	 */
	public String getCustomerCodeNameByCodeFuzzy(String code, int size, int page);
}
