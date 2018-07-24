package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.CustomerDistrService;

@Controller
public class CustomerDistrController {
	@Autowired
	private CustomerDistrService customerDistrService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/chance/saveCustomerDistr.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerDistrService.saveCustomerDistr(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/crm/chance/deleteCustomerDistr.action")
	@ResponseBody
	public Map<String, Object> deleteCustomerDistr(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerDistrService.deleteCustomerDistr(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/chance/updateCustomerDistr.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerDistrService.updateCustomerDistr(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
