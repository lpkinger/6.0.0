package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProdAbnormalService;

@Controller
public class ProdAbnormalController {
	@Autowired
	private ProdAbnormalService prodAbnormalService;

	/**
	 * 保存ProdAbnormal
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/qc/saveProdAbnormal.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAbnormalService.saveProdAbnormal(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/scm/qc/deleteProdAbnormal.action")
	@ResponseBody
	public Map<String, Object> deleteProdAbnormal(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAbnormalService.deleteProdAbnormal(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/scm/qc/updateProdAbnormal.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAbnormalService.updateProdAbnormalById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/qc/submitProdAbnormal.action")
	@ResponseBody
	public Map<String, Object> submitProdAbnormal(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAbnormalService.submitProdAbnormal(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/qc/resSubmitProdAbnormal.action")
	@ResponseBody
	public Map<String, Object> resSubmitProdAbnormal(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAbnormalService.resSubmitProdAbnormal(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/qc/auditProdAbnormal.action")
	@ResponseBody
	public Map<String, Object> auditProdAbnormal(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAbnormalService.auditProdAbnormal(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/qc/resAuditProdAbnormal.action")
	@ResponseBody
	public Map<String, Object> resAuditProdAbnormal(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAbnormalService.resAuditProdAbnormal(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批准
	 */
	@RequestMapping("/scm/qc/checkProdAbnormal.action")
	@ResponseBody
	public Map<String, Object> checkProdAbnormal(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAbnormalService.checkProdAbnormal(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反批准
	 */
	@RequestMapping("/scm/qc/resCheckProdAbnormal.action")
	@ResponseBody
	public Map<String, Object> resCheckProdAbnormal(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAbnormalService.resCheckProdAbnormal(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
