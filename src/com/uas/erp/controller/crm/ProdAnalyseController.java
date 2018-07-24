package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.ProdAnalyseService;

@Controller
public class ProdAnalyseController {
	@Autowired
	private ProdAnalyseService prodAnalyseService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/marketmgr/saveProdAnalyse.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAnalyseService.saveProdAnalyse(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ECN数据 包括ECN明细
	 */
	@RequestMapping("/crm/marketmgr/deleteProdAnalyse.action")
	@ResponseBody
	public Map<String, Object> deleteProdAnalyse(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAnalyseService.deleteProdAnalyse(id, caller);
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
	@RequestMapping("/crm/marketmgr/updateProdAnalyse.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAnalyseService.updateProdAnalyse(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/marketmgr/submitProdAnalyse.action")
	@ResponseBody
	public Map<String, Object> submitProdAnalyse(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAnalyseService.submitProdAnalyse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/marketmgr/resSubmitProdAnalyse.action")
	@ResponseBody
	public Map<String, Object> resSubmitProdAnalyse(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAnalyseService.resSubmitProdAnalyse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/marketmgr/auditProdAnalyse.action")
	@ResponseBody
	public Map<String, Object> auditProdAnalyse(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAnalyseService.auditProdAnalyse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/marketmgr/resAuditProdAnalyse.action")
	@ResponseBody
	public Map<String, Object> resAuditProdAnalyse(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodAnalyseService.resAuditProdAnalyse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
