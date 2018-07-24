package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.oa.KnowledgeService;

@Controller
public class KnowledgeController {
	@Autowired
	private KnowledgeService knowledgeService;

	@RequestMapping("/oa/knowledge/saveKnowledge.action")
	@ResponseBody
	public Map<String, Object> saveKnowledge(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		knowledgeService.saveKnowledge(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/knowledge/deleteKnowledge.action")
	@ResponseBody
	public Map<String, Object> deleteKnowledge(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		knowledgeService.deleteKnowledge(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/knowledge/updateKnowledge.action")
	@ResponseBody
	public Map<String, Object> updateKnowledge(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		knowledgeService.updateKnowledge(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/knowledge/submitKnowledge.action")
	@ResponseBody
	public Map<String, Object> submitKnowledge(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		knowledgeService.submitKnowledge(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/knowledge/resSubmitKnowledge.action")
	@ResponseBody
	public Map<String, Object> resSubmitKnowledge(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		knowledgeService.resSubmitKnowledge(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/knowledge/auditKnowledge.action")
	@ResponseBody
	public Map<String, Object> auditKnowledge(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		knowledgeService.auditKnowledge(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/knowledge/resAuditKnowledge.action")
	@ResponseBody
	public Map<String, Object> resAuditKnowledge(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		knowledgeService.resAuditKnowledge(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	// 推荐
	@RequestMapping("/oa/knowledge/recommendKnowledge.action")
	@ResponseBody
	public Map<String, Object> recommendKnowledge(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		knowledgeService.recommendKnowledge(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/knowledge/VastDeleteKnowledgeModule.action")
	@ResponseBody
	public Map<String, Object> VastDeleteKnowledge(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		knowledgeService.VastDeleteKnowledgeModule(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/knowledge/getKnowledgeModule.action")
	@ResponseBody
	public Map<String, Object> getKnowledgeModule(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = knowledgeService.getJSONModule(caller);
		modelMap.put("tree", tree);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/knowledge/SaveComment.action")
	@ResponseBody
	public Map<String, Object> saveComment(String caller,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		knowledgeService.saveKnowledgeComment(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/knowledge/SaveRecommend.action")
	@ResponseBody
	public Map<String, Object> SaveRecommend(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		knowledgeService.saveKnowledgeRecommend(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
