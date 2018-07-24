package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.CustomerMarksService;

@Controller
public class CustomerMarksController {
	@Autowired
	private CustomerMarksService customerMarksService;
	/**
	 * 保存CustomerMarks
	 * @param formStore form数据
	 */
	@RequestMapping("/scm/sale/saveCustomerMarks.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerMarksService.saveCustomerMarks(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/sale/updateCustomerMarks.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerMarksService.updateCustomerMarksById(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteCustomerMarks.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerMarksService.deleteCustomerMarks(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
