package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.SampleapplyService;

@Controller
public class SampleapplyController {

	@Autowired
	private SampleapplyService sampleapplyService;

	/**
	 * 保存Sampleapply
	 */
	@RequestMapping("/scm/product/saveSampleapply.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleapplyService.saveSampleapply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/scm/product/updateSampleapply.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleapplyService.updateSampleapplyById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deleteSampleapply.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleapplyService.deleteSampleapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitSampleapply.action")
	@ResponseBody
	public Map<String, Object> submitSampleapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleapplyService.submitSampleapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitSampleapply.action")
	@ResponseBody
	public Map<String, Object> resSubmitSampleapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleapplyService.resSubmitSampleapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/product/auditSampleapply.action")
	@ResponseBody
	public Map<String, Object> auditSampleapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleapplyService.auditSampleapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditSampleapply.action")
	@ResponseBody
	public Map<String, Object> resAuditSampleapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleapplyService.resAuditSampleapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转打样申请单
	 */
	@RequestMapping("/scm/product/turnProductSample.action")
	@ResponseBody
	public Map<String, Object> turnProductSample(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = sampleapplyService.turnProductSample(data, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}

	/**
	 * 转物料认定单
	 */
	@RequestMapping("/scm/product/sampleapply/turnProductApproval.action")
	@ResponseBody
	public Map<String, Object> turnProductApproval(HttpSession session,
			String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = sampleapplyService.turnProductApproval(data);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
}
