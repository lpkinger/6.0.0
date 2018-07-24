package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.CustomerService;

@Controller
public class CustomerController {
	@Autowired
	private CustomerService customerService;
	/**
	 * 保存customer
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/scm/sale/saveCustomer.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerService.saveCustomer(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改customer
	 */
	@RequestMapping("/scm/sale/updateCustomer.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerService.updateCustomer(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改customer UU
	 * 
	 * @param uu
	 *            客户UU号
	 */
	@RequestMapping("/scm/customer/updateUU.action")
	@ResponseBody
	public Map<String, Object> updateUU(String caller, Integer id, String uu,String cu_businesscode,String cu_lawman,String cu_add1) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerService.updateUU(id,uu,caller,cu_businesscode,cu_lawman,cu_add1);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 获取客户打印标签编号
	 * 
	 * @param condition
	 */
	@RequestMapping("/scm/customer/getCustLabelCode.action")
	@ResponseBody
	public Map<String, Object> getCustLabelCode(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",customerService.getCustLabelCode(condition));
		modelMap.put("success", true);
		return modelMap;
	}
	
}
