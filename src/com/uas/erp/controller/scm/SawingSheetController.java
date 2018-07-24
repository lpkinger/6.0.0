package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.SawingSheetService;

@Controller
public class SawingSheetController extends BaseController {
	@Autowired
	private SawingSheetService sawingSheetService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/reserve/saveSawingSheet.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sawingSheetService.saveSawingSheet(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/reserve/deleteSawingSheet.action")
	@ResponseBody
	public Map<String, Object> deleteSawingSheet(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sawingSheetService.deleteSawingSheet(caller, id);
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
	@RequestMapping("/scm/reserve/updateSawingSheet.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sawingSheetService.updateSawingSheetById(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/reserve/submitSawingSheet.action")
	@ResponseBody
	public Map<String, Object> submitSawingSheet(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sawingSheetService.submitSawingSheet(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/reserve/resSubmitSawingSheet.action")
	@ResponseBody
	public Map<String, Object> resSubmitSawingSheet(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sawingSheetService.resSubmitSawingSheet(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/reserve/auditSawingSheet.action")
	@ResponseBody
	public Map<String, Object> auditSawingSheet(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sawingSheetService.auditSawingSheet(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/reserve/resAuditSawingSheet.action")
	@ResponseBody
	public Map<String, Object> resAuditSawingSheet(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sawingSheetService.resAuditSawingSheet(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/scm/reserve/postSawingSheet.action")
	@ResponseBody
	public Map<String, Object> postSawingSheet(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sawingSheetService.postSawingSheet(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/scm/reserve/resPostSawingSheet.action")
	@ResponseBody
	public Map<String, Object> resPostSawingSheet(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sawingSheetService.resPostSawingSheet(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
}
