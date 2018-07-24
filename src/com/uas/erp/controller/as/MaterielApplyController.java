package com.uas.erp.controller.as;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.as.MaterielApplyService;

@Controller
public class MaterielApplyController extends BaseController {
	@Autowired
	private MaterielApplyService materielApplyService;
	/**
	 * 保存
	 */
	@RequestMapping("/as/port/saveMaterielApply.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller,String param, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielApplyService.saveMaterielApply(formStore,param,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/as/port/updateMaterielApply.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String param,String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielApplyService.updateMaterielApplyById(formStore,param, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/as/port/deleteMaterielApply.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielApplyService.deleteMaterielApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交操作
	 */
	@RequestMapping("/as/port/submitMaterielApply.action")
	@ResponseBody
	public Map<String, Object> submitMaterielApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielApplyService.submitMaterielApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交操作
	 */
	@RequestMapping("/as/port/resSubmitMaterielApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitMaterielApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielApplyService.resSubmitMaterielApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 审核
	 */
	@RequestMapping("/as/port/auditMaterielApply.action")  
	@ResponseBody 
	public Map<String, Object> auditMaterielApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielApplyService.auditMaterielApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核操作
	 */
	@RequestMapping("/as/port/resAuditMaterielApply.action")
	@ResponseBody
	public Map<String, Object> resAuditMaterielApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielApplyService.resAuditMaterielApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

