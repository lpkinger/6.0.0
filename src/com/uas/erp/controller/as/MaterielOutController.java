package com.uas.erp.controller.as;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.as.MaterielOutService;

@Controller
public class MaterielOutController extends BaseController {
	@Autowired
	private MaterielOutService materielOutService;
	/**
	 * 保存
	 */
	@RequestMapping("/as/port/saveMaterielOut.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String param,String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielOutService.saveMaterielOut(formStore,param,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/as/port/updateMaterielOut.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String param,String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielOutService.updateMaterielOutById(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/as/port/deleteMaterielOut.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielOutService.deleteMaterielOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交操作
	 */
	@RequestMapping("/as/port/submitMaterielOut.action")
	@ResponseBody
	public Map<String, Object> submitMaterielOut(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielOutService.submitMaterielOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交操作
	 */
	@RequestMapping("/as/port/resSubmitMaterielOut.action")
	@ResponseBody
	public Map<String, Object> resSubmitMaterielOut(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielOutService.resSubmitMaterielOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 审核
	 */
	@RequestMapping("/as/port/auditMaterielOut.action")  
	@ResponseBody 
	public Map<String, Object> auditMaterielOut(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielOutService.auditMaterielOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核操作
	 */
	@RequestMapping("/as/port/resAuditMaterielOut.action")
	@ResponseBody
	public Map<String, Object> resAuditMaterielOut(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielOutService.resAuditMaterielOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新明细行
	 * */

	@RequestMapping("/as/port/updateMaterialQtyChangeInProcss.action")
	@ResponseBody
	public Map<String, Object> updateMaterialQtyChangeInProcss(String caller,
			String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materielOutService.updateMaterialQtyChangeInProcss(caller,
				formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
}

