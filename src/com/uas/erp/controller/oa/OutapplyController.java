package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.OutapplyService;

@Controller
public class OutapplyController {

	@Autowired
	private OutapplyService outapplyService;

	/**
	 * 保存oaOrg
	 */
	@RequestMapping("/oa/check/saveOutapply.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		outapplyService.saveOutapply(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/oa/check/updateOutapply.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		outapplyService.updateOutapplyById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/check/deleteOutapply.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
		outapplyService.deleteOutapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/check/submitOutapply.action")
	@ResponseBody
	public Map<String, Object> submitOutapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		outapplyService.submitOutapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/check/resSubmitOutapply.action")
	@ResponseBody
	public Map<String, Object> resSubmitOutapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		outapplyService.resSubmitOutapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/check/auditOutapply.action")
	@ResponseBody
	public Map<String, Object> auditOutapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		outapplyService.auditOutapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/check/resAuditOutapply.action")
	@ResponseBody
	public Map<String, Object> resAuditOutapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		outapplyService.resAuditOutapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
