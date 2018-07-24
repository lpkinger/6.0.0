package com.uas.erp.controller.as;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.as.MaterielReturnService;

@Controller
public class MaterielReturnController extends BaseController {
	@Autowired
	private MaterielReturnService materielReturnService;
	/**
	 * 保存
	 */
	@RequestMapping("/as/port/saveMaterielReturn.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller,String param, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielReturnService.saveMaterielReturn(formStore,param,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/as/port/updateMaterielReturn.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String param,String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielReturnService.updateMaterielReturnById(formStore,param, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/as/port/deleteMaterielReturn.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielReturnService.deleteMaterielReturn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交操作
	 */
	@RequestMapping("/as/port/submitMaterielReturn.action")
	@ResponseBody
	public Map<String, Object> submitMaterielReturn(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielReturnService.submitMaterielReturn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交操作
	 */
	@RequestMapping("/as/port/resSubmitMaterielReturn.action")
	@ResponseBody
	public Map<String, Object> resSubmitMaterielReturn(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielReturnService.resSubmitMaterielReturn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 审核
	 */
	@RequestMapping("/as/port/auditMaterielReturn.action")  
	@ResponseBody 
	public Map<String, Object> auditMaterielReturn(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielReturnService.auditMaterielReturn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核操作
	 */
	@RequestMapping("/as/port/resAuditMaterielReturn.action")
	@ResponseBody
	public Map<String, Object> resAuditMaterielReturn(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielReturnService.resAuditMaterielReturn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

