package com.uas.erp.ac.service.common;

import java.util.Map;

public interface CustomerInfoService {

	/**
	 * 分页获取客户信息
	 * 
	 * @param keyword
	 * @param start
	 * @param page
	 * @param limit
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> customers(String keyword, Integer page, Integer limit) throws Exception;
	public Map<String, Object> getCustomerData(String caller, String condition);
	public void updateCustomerData(String id, String uu);
	public Map<String, Object> customerUse(Integer id, Integer hasRelative,Integer type, String vendUID) throws Exception;
	public Map<String, Object> erpCustomers(String keyword, Integer page,
			Integer limit);
}
