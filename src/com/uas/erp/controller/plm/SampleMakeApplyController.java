package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.SampleMakeApplyService;

@Controller
public class SampleMakeApplyController {
	@Autowired
	private SampleMakeApplyService sampleMakeApplyService;
	
	/**
	 * 保存
	 * @param caller
	 * @param formStore
	 * @return
	 */
	@RequestMapping("plm/request/saveSampleMakeApply.action")
	@ResponseBody
	public Map<String,Object> save(String caller, String formStore){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleMakeApplyService.saveSampleMakeApply(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 * 更新
	 */
	@RequestMapping("/plm/request/updateSampleMakeApply.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleMakeApplyService.updateSampleMakeApplyById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/plm/request/deleteSampleMakeApply.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleMakeApplyService.deleteSampleMakeApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交操作
	 */
	@RequestMapping("/plm/request/submitSampleMakeApply.action")
	@ResponseBody
	public Map<String, Object> submitSampleMakeApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleMakeApplyService.submitSampleMakeApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交操作
	 */
	@RequestMapping("/plm/request/resSubmitSampleMakeApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitSampleMakeApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleMakeApplyService.resSubmitSampleMakeApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 审核
	 */
	@RequestMapping("/plm/request/auditSampleMakeApply.action")  
	@ResponseBody 
	public Map<String, Object> auditSampleMakeApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleMakeApplyService.auditSampleMakeApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核操作
	 */
	@RequestMapping("/plm/request/resAuditSampleMakeApply.action")
	@ResponseBody
	public Map<String, Object> resAuditSampleMakeApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleMakeApplyService.resAuditSampleMakeApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转请购单
	 */
	@RequestMapping("/plm/request/turnApplication.action")
	@ResponseBody
	public Map<String, Object> turnApplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",sampleMakeApplyService.turnApplication(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转制造单
	 */
	@RequestMapping("/plm/request/turnMake.action")
	@ResponseBody
	public Map<String, Object> turnMake(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",sampleMakeApplyService.turnMake(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转其他出库
	 */
	@RequestMapping("/plm/request/turnOtherOut.action")
	@ResponseBody
	public Map<String, Object> turnOtherOut(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",sampleMakeApplyService.turnOtherOut(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
