package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.LineService;

@Controller
public class LineController {

	@Autowired
	private LineService lineService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/saveLine.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lineService.saveLine(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/pm/mes/deleteLine.action")
	@ResponseBody
	public Map<String, Object> deleteLineio(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	lineService.deleteLine(id, caller);
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
	@RequestMapping("/pm/mes/updateLine.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lineService.updateLineById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitLine.action")
	@ResponseBody
	public Map<String, Object> submitLineio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lineService.submitLine(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitLine.action")
	@ResponseBody
	public Map<String, Object> resSubmitLineio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lineService.resSubmitLine(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditLine.action")
	@ResponseBody
	public Map<String, Object> auditLineio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lineService.auditLine(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditLine.action")
	@ResponseBody
	public Map<String, Object> resAuditLineio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lineService.resAuditLine(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
