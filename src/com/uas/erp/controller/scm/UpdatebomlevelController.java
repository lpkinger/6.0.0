package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.UpdatebomlevelService;

@Controller
public class UpdatebomlevelController {
	
	@Autowired
	private UpdatebomlevelService updatebomlevelService;
	/**
	 * 保存Updatebomlevel
	 */
	@RequestMapping("/scm/product/saveUpdatebomlevel.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updatebomlevelService.saveUpdatebomlevel(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/product/updateUpdatebomlevel.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore,String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updatebomlevelService.updateUpdatebomlevelById(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deleteUpdatebomlevel.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updatebomlevelService.deleteUpdatebomlevel(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitUpdatebomlevel.action")  
	@ResponseBody 
	public Map<String, Object> submitUpdatebomlevel(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updatebomlevelService.submitUpdatebomlevel(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitUpdatebomlevel.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitUpdatebomlevel(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updatebomlevelService.resSubmitUpdatebomlevel(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/product/auditUpdatebomlevel.action")  
	@ResponseBody 
	public Map<String, Object> auditUpdatebomlevel(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updatebomlevelService.auditUpdatebomlevel(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditUpdatebomlevel.action")  
	@ResponseBody 
	public Map<String, Object> resAuditUpdatebomlevel(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updatebomlevelService.resAuditUpdatebomlevel(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
