package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.oa.PowerApplyService;

@Controller
public class PowerApplyController {
	@Autowired
	private PowerApplyService powerApplyService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/powerApply/savePowerApply.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		powerApplyService.savePowerApply(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/powerApply/updatePowerApply.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		powerApplyService.updatePowerApply(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/powerApply/deletePowerApply.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		powerApplyService.deletePowerApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/powerApply/auditPowerApply.action")  
	@ResponseBody 
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		powerApplyService.auditPowerApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/powerApply/resAuditPowerApply.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(String caller, int id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		powerApplyService.resAuditPowerApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/powerApply/submitPowerApply.action")  
	@ResponseBody 
	public Map<String, Object> submit(String caller, int id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		powerApplyService.submitPowerApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/powerApply/resSubmitPowerApply.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(String caller, int id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		powerApplyService.resSubmitPowerApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
