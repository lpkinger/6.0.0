package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.BadCodeService;

@Controller
public class BadCodeController {

	@Autowired
	private BadCodeService badCodeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/saveBadCode.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		badCodeService.saveBadCode(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/pm/mes/deleteBadCode.action")
	@ResponseBody
	public Map<String, Object> deleteBadCodeio(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	badCodeService.deleteBadCode(id, caller);
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
	@RequestMapping("/pm/mes/updateBadCode.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		badCodeService.updateBadCodeById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitBadCode.action")
	@ResponseBody
	public Map<String, Object> submitBadCodeio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		badCodeService.submitBadCode(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitBadCode.action")
	@ResponseBody
	public Map<String, Object> resSubmitBadCodeio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		badCodeService.resSubmitBadCode(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditBadCode.action")
	@ResponseBody
	public Map<String, Object> auditBadCodeio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		badCodeService.auditBadCode(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditBadCode.action")
	@ResponseBody
	public Map<String, Object> resAuditBadCodeio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		badCodeService.resAuditBadCode(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
