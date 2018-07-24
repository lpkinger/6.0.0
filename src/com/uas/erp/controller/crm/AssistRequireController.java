package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.AssistRequireService;

@Controller
public class AssistRequireController {
	@Autowired
	private AssistRequireService assistRequireService;

	// 规范 小写
	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("crm/customermgr/saveAssistRequire.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		assistRequireService.saveAssistRequire(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/crm/customermgr/deleteAssistRequire.action")
	@ResponseBody
	public Map<String, Object> deleteAssistRequire(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		assistRequireService.deleteAssistRequire(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/crm/customermgr/updateAssistRequire.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		assistRequireService.updateAssistRequireById(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/customermgr/submitAssistRequire.action")
	@ResponseBody
	public Map<String, Object> submitAssistRequire(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		assistRequireService.submitAssistRequire(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/customermgr/resSubmitAssistRequire.action")
	@ResponseBody
	public Map<String, Object> resSubmitAssistRequire(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		assistRequireService.resSubmitAssistRequire(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/customermgr/auditAssistRequire.action")
	@ResponseBody
	public Map<String, Object> auditAssistRequire(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		assistRequireService.auditAssistRequire(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/customermgr/resAuditAssistRequire.action")
	@ResponseBody
	public Map<String, Object> resAuditAssistRequire(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assistRequireService.resAuditAssistRequire(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
