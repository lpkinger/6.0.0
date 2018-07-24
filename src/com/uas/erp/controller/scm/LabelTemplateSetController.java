package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.LabelTemplateSetService;

@Controller
public class LabelTemplateSetController {
	@Autowired
	private LabelTemplateSetService  LabelTemplateSetService;
	
	/**
	 * 保存标签打印模板设置
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/scm/reserve/saveLabelT.action")  
	@ResponseBody 
	public Map<String, Object> saveLabelT(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		LabelTemplateSetService.saveLabelT(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新标签打印模板设置
	 */
	@RequestMapping("/scm/reserve/updateLabelT.action")  
	@ResponseBody 
	public Map<String, Object> updateLabelT(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		LabelTemplateSetService.updateLabelT(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	

	@RequestMapping("/scm/reserve/deleteLabelT.action")  
	@ResponseBody 
	public Map<String, Object> deleteLabelT(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		LabelTemplateSetService.deleteLabelT(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/auditLabelT.action")  
	@ResponseBody 
	public Map<String, Object> auditLabelT(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		LabelTemplateSetService.auditLabelT(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/scm/reserve/resAuditLabelT.action")  
	@ResponseBody 
	public Map<String, Object> resAuditLabelT(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		LabelTemplateSetService.resAuditLabelT(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/scm/reserve/bannedLabelT.action")  
	@ResponseBody 
	public Map<String, Object> bannedLabelT(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		LabelTemplateSetService.bannedSerial(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/resBannedLabelT.action")  
	@ResponseBody 
	public Map<String, Object> resBannedLabelT(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		LabelTemplateSetService.resBannedLabelT(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/submitLabelT.action")  
	@ResponseBody 
	public Map<String, Object> submitLabelT(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		LabelTemplateSetService.submitLabelT(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/resSubmitLabelT.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitLabelT(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		LabelTemplateSetService.resSubmitLabelT(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/saveLabelP.action")  
	@ResponseBody 
	public Map<String, Object> saveLabelP(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		LabelTemplateSetService.saveLabelP(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/updateLabelP.action")  
	@ResponseBody 
	public Map<String, Object> updateLabelP(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		LabelTemplateSetService.updateLabelP(caller,formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/deleteLabelP.action")  
	@ResponseBody 
	public Map<String, Object> deleteLabelP(String caller, int  id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		LabelTemplateSetService.deleteLabelP(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
		
	@RequestMapping("/scm/reserve/getdetail.action")  
	@ResponseBody 
	public Map<String, Object> getdetail(String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", LabelTemplateSetService.getdetail(caller,condition));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/saveLabelPrintSetting.action")  
	@ResponseBody 
	public Map<String, Object> saveLPrintSetting(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		LabelTemplateSetService.saveLPrintSetting(caller,param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/deleteLabelPrintSetting.action")  
	@ResponseBody 
	public Map<String, Object> deleteLabelPrintSetting(String caller, String lps_caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		LabelTemplateSetService.deleteLabelPrintSetting(caller,lps_caller);
		modelMap.put("success", true);
		return modelMap;
	}

	

}
