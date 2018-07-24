package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.CustomzlService;

@Controller
public class CustomzlController {
	@Autowired
	private CustomzlService customzlService;
	/**
	 * 保存customzl
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/scm/product/saveCustomzl.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customzlService.saveCustomzl(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/product/updateCustomzl.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customzlService.updateCustomzlById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deleteCustomzl.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customzlService.deleteCustomzl(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 区间日期计算
	 */
	@RequestMapping("/scm/product/calculateDate.action")  
	@ResponseBody 
	public Map<String, Object> calculateDate(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customzlService.calculateDate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
