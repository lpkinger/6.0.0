package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.PropertyapplyService;

@Controller
public class PropertyapplyController {

	@Autowired
	private PropertyapplyService propertyapplyService;

	/**
	 * 保存oaOrg
	 */
	@RequestMapping("/oa/storage/savePropertyapply.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyapplyService.savePropertyapply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/oa/storage/updatePropertyapply.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyapplyService.updatePropertyapplyById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/storage/deletePropertyapply.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyapplyService.deletePropertyapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/storage/submitPropertyapply.action")
	@ResponseBody
	public Map<String, Object> submitPropertyapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyapplyService.submitPropertyapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/storage/resSubmitPropertyapply.action")
	@ResponseBody
	public Map<String, Object> resSubmitPropertyapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyapplyService.resSubmitPropertyapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/storage/auditPropertyapply.action")
	@ResponseBody
	public Map<String, Object> auditPropertyapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyapplyService.auditPropertyapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/storage/resAuditPropertyapply.action")
	@ResponseBody
	public Map<String, Object> resAuditPropertyapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyapplyService.resAuditPropertyapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 领用资产
	 */
	@RequestMapping("/oa/storage/Propertyget.action")
	@ResponseBody
	public Map<String, Object> getProperty(String caller, int id, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyapplyService.getProperty(id, caller, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 归还资产
	 */
	@RequestMapping("/oa/storage/ReturnProperty.action")
	@ResponseBody
	public Map<String, Object> ReturnProperty(String caller, int id,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyapplyService.ReturnProperty(id, caller, param);
		modelMap.put("success", true);
		return modelMap;
	}

}
