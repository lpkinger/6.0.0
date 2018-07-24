package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.KBIAssessService;

@Controller
public class KBIAssessController {
	@Autowired
	private KBIAssessService kbiAssessService;

	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/hr/kbi/saveKBIAssess.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbiAssessService.saveKBIAssess(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/kbi/deleteKBIAssess.action")
	@ResponseBody
	public Map<String, Object> deleteKBIAssess(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbiAssessService.deleteKBIAssess(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/hr/kbi/updateKBIAssess.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbiAssessService.updateKBIAssessById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/kbi/submitKBIAssess.action")
	@ResponseBody
	public Map<String, Object> submitKBIAssess(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbiAssessService.submitKBIAssess(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/kbi/resSubmitKBIAssess.action")
	@ResponseBody
	public Map<String, Object> resSubmitKBIAssess(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbiAssessService.resSubmitKBIAssess(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/kbi/auditKBIAssess.action")
	@ResponseBody
	public Map<String, Object> auditKBIAssess(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbiAssessService.auditKBIAssess(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/kbi/resAuditKBIAssess.action")
	@ResponseBody
	public Map<String, Object> resAuditKBIAssess(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbiAssessService.resAuditKBIAssess(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转评估单
	 */
	@RequestMapping("/hr/kbi/turnKBIbill.action")
	@ResponseBody
	public Map<String, Object> turnKBIbill(String caller, 
			String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbiAssessService.turnKBIBill(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据填写的客户和录入人，获取符合条件的最新记录。再一次插入数据库中，简化了录入工作
	 */
	@RequestMapping("/hr/kbi/autoSaveKBIAssess.action")
	@ResponseBody
	public Map<String, Object> autoSave(String caller, String ka_detp) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int ka_id = kbiAssessService.autoSave(caller, ka_detp);
		modelMap.put("success", true);
		modelMap.put("ka_id", ka_id);
		return modelMap;
	}
}
