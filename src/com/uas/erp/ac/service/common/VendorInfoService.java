package com.uas.erp.ac.service.common;

import java.util.Map;

import org.springframework.ui.ModelMap;

public interface VendorInfoService {

	/**
	 * 分页获取供应商信息
	 * 
	 * @param keyword
	 * @param start
	 * @param page
	 * @param limit
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> vendors(String keyword, Integer pageNumber, Integer pageSize) throws Exception;
	public Map<String, Object> getVendorData(String caller, String condition);
	public void updateVendorData(String id, String uu);
	public ModelMap vendUse(Integer id, Integer hasRelative, Integer type, String vendUID) throws Exception;
	public Map<String, Object> erpVendors(String keyword, Integer page,
			Integer limit);
	public Map<String, Object> services(String keyword, Integer page,
			Integer limit);
	public Map<String, Object> serviceUse(Integer id, Integer hasRelative,
			Integer type, String vendUID);
}
