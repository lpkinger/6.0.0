package com.uas.erp.controller.ac;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uas.erp.ac.service.common.CustomerInfoService;

/**
 * 企业圈的客户
 * 
 * @author hejq
 * @time 创建时间：2017年6月7日
 */
@RestController
public class CustomerInfoController {

	@Autowired
	private CustomerInfoService customerInfoService;

	/**
	 * 客户
	 */
	@RequestMapping("ac/customers.action")
	public Map<String, Object> customers(String keyword, Integer page, Integer limit) throws Exception {
		return customerInfoService.customers(keyword, page, limit);
	}
	/**
	 * erp客户
	 */
	@RequestMapping("ac/erpCustomers.action")
	public Map<String, Object> erpCustomers(String keyword, Integer page, Integer limit) throws Exception {
		return customerInfoService.erpCustomers(keyword, page, limit);
	}
	@RequestMapping("ac/getCustomerData.action")
	public Map<String, Object> getCustomerData(String caller,String condition) {
		return customerInfoService.getCustomerData(caller, condition);
	}
	@RequestMapping("ac/updateCustomerData.action")
	public Map<String, Object> updateCustomerData(String id,String uu) {
		Map<String,Object> map = new HashMap<String, Object>();
		customerInfoService.updateCustomerData(id, uu);
		map.put("success",true);
		return map;
	}
	@RequestMapping("ac/customerUse.action")
	public Map<String,Object> customerUse(Integer id,Integer hasRelative,Integer type,String vendUID) throws Exception{
		return customerInfoService.customerUse(id,hasRelative,type,vendUID);
	}
}
