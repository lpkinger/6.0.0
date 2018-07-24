package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.CommonChangeService;

/**
 * 通用变更单
 * */
@Controller
public class CommonChangeController {
	@Autowired
	private CommonChangeService commonChangeService;

	@RequestMapping("/common/saveCommonChange.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonChangeService.saveCommonChange(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/common/deleteCommonChange.action")
	@ResponseBody
	public Map<String, Object> deleteCommon(HttpSession session, String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonChangeService.deleteCommonChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/common/updateCommonChange.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonChangeService.updateCommonChange(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 * */
	@RequestMapping("/common/submitCommonChange.action")
	@ResponseBody
	public Map<String, Object> submitCommon(HttpSession session, String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonChangeService.submitCommonChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 * */
	@RequestMapping("/common/resSubmitCommonChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitCommonChange(HttpSession session, String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonChangeService.resSubmitCommonChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/common/auditCommonChange.action")
	@ResponseBody
	public Map<String, Object> auditCommon(HttpSession session, String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonChangeService.auditCommonChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/common/resAuditCommonChange.action")
	@ResponseBody
	public Map<String, Object> resAuditCommon(HttpSession session, String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonChangeService.resAuditCommonChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
}
