package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.UpdateprodlevelService;

@Controller
public class UpdateprodlevelController {
	
	@Autowired
	private UpdateprodlevelService updateprodlevelService;
	/**
	 * 保存Updateprodlevel
	 */
	@RequestMapping("/scm/product/saveUpdateprodlevel.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updateprodlevelService.saveUpdateprodlevel(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/product/updateUpdateprodlevel.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore,String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updateprodlevelService.updateUpdateprodlevelById(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deleteUpdateprodlevel.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updateprodlevelService.deleteUpdateprodlevel(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitUpdateprodlevel.action")  
	@ResponseBody 
	public Map<String, Object> submitUpdateprodlevel(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updateprodlevelService.submitUpdateprodlevel(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitUpdateprodlevel.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitUpdateprodlevel(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updateprodlevelService.resSubmitUpdateprodlevel(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/product/auditUpdateprodlevel.action")  
	@ResponseBody 
	public Map<String, Object> auditUpdateprodlevel(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updateprodlevelService.auditUpdateprodlevel(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditUpdateprodlevel.action")  
	@ResponseBody 
	public Map<String, Object> resAuditUpdateprodlevel(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updateprodlevelService.resAuditUpdateprodlevel(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
