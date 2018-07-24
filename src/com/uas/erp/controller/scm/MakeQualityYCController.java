package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.scm.MakeQualityYCService;

@Controller
public class MakeQualityYCController {
	@Autowired
	private MakeQualityYCService makeQualityYCService ;
	/**
	 * 保存ComplaintRecords
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/scm/qc/saveMakeQualityYC.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeQualityYCService.saveMakeQualityYC(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/qc/updateMakeQualityYC.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeQualityYCService.updateMakeQualityYCById(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/qc/deleteMakeQualityYC.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeQualityYCService.deleteMakeQualityYC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/qc/submitMakeQualityYC.action")  
	@ResponseBody 
	public Map<String, Object> submitMakeQualityYC(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeQualityYCService.submitMakeQualityYC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/qc/resSubmitMakeQualityYC.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitMakeQualityYC(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeQualityYCService.resSubmitMakeQualityYC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/qc/auditMakeQualityYC.action")  
	@ResponseBody 
	public Map<String, Object> auditMakeQualityYC(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeQualityYCService.auditMakeQualityYC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/qc/resAuditMakeQualityYC.action")  
	@ResponseBody 
	public Map<String, Object> resAuditMakeQualityYC(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeQualityYCService.resAuditMakeQualityYC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
