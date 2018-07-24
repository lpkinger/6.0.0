package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.PrjManChangeService;

@Controller
public class PrjManChangeController {
	@Autowired
	private PrjManChangeService prjManChangeService;

	/**
	 * 保存
	 */
	@RequestMapping("/plm/project/savePrjManChange.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prjManChangeService.savePrjManChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/plm/project/deletePrjManChange.action")
	@ResponseBody
	public Map<String, Object> deletePrjManChange(HttpSession session, int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prjManChangeService.deletePrjManChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/plm/project/updatePrjManChange.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prjManChangeService.updatePrjManChangeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/plm/project/submitPrjManChange.action")
	@ResponseBody
	public Map<String, Object> submitPrjManChange(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prjManChangeService.submitPrjManChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/plm/project/resSubmitPrjManChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitPrjManChange(HttpSession session,
			int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prjManChangeService.resSubmitPrjManChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/plm/project/auditPrjManChange.action")
	@ResponseBody
	public Map<String, Object> auditPrjManChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prjManChangeService.auditPrjManChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/plm/project/resAuditPrjManChange.action")
	@ResponseBody
	public Map<String, Object> resAuditPrjManChange(HttpSession session,
			int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prjManChangeService.resAuditPrjManChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
