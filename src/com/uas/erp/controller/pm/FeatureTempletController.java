package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.FeatureTempletService;

@Controller
public class FeatureTempletController extends BaseController {
	@Autowired
	private FeatureTempletService featureTempletService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveFeatureTemplet.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureTempletService.saveFeatureTemplet(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/pm/bom/deleteFeatureTemplet.action")
	@ResponseBody
	public Map<String, Object> deleteFeatureTemplet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureTempletService.deleteFeatureTemplet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/updateFeatureTemplet.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureTempletService.updateFeatureTempletById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/bom/submitFeatureTemplet.action")
	@ResponseBody
	public Map<String, Object> submitFeatureTemplet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureTempletService.submitFeatureTemplet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/bom/resSubmitFeatureTemplet.action")
	@ResponseBody
	public Map<String, Object> resSubmitFeatureTemplet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureTempletService.resSubmitFeatureTemplet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/bom/auditFeatureTemplet.action")
	@ResponseBody
	public Map<String, Object> auditFeatureTemplet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureTempletService.auditFeatureTemplet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/bom/resAuditFeatureTemplet.action")
	@ResponseBody
	public Map<String, Object> resAuditFeatureTemplet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureTempletService.resAuditFeatureTemplet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
