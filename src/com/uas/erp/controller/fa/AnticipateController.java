package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.AnticipateService;

@Controller
public class AnticipateController extends BaseController {
	@Autowired
	private AnticipateService anticipateService;

	@RequestMapping("/fa/fp/createAnticipate.action")
	@ResponseBody
	public Map<String, Object> createAnticipate(HttpSession session, String date, String cucode, String emcode, String dpcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		anticipateService.createAnticipate(date, cucode, emcode, dpcode);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/fp/saveAnticipate.action")
	@ResponseBody
	public Map<String, Object> saveAnticipate(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		anticipateService.saveAnticipate(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 */
	@RequestMapping("/fa/fp/deleteAnticipate.action")
	@ResponseBody
	public Map<String, Object> deleteAnticipate(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		anticipateService.deleteAnticipate(id, caller);
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
	@RequestMapping("/fa/fp/updateAnticipate.action")
	@ResponseBody
	public Map<String, Object> updateAnticipate(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		anticipateService.updateAnticipateById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fp/submitAnticipate.action")
	@ResponseBody
	public Map<String, Object> submitAnticipate(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		anticipateService.submitAnticipate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fp/resSubmitAnticipate.action")
	@ResponseBody
	public Map<String, Object> resSubmitAnticipate(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		anticipateService.resSubmitAnticipate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fp/auditAnticipate.action")
	@ResponseBody
	public Map<String, Object> auditAnticipate(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		anticipateService.auditAnticipate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fp/resAuditAnticipate.action")
	@ResponseBody
	public Map<String, Object> resAuditAnticipate(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		anticipateService.resAuditAnticipate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/fp/printAnticipate.action")
	@ResponseBody
	public Map<String, Object> printAnticipate(HttpSession session, int id, String caller, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = anticipateService.printAnticipate(id, reportName, condition, caller);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 逾期回款刷新
	 */
	@RequestMapping(value = "/fa/fp/refreshAnticipateBack.action")
	@ResponseBody
	public Map<String, Object> refreshAnticipateBack(String caller, String from, String to) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		anticipateService.refreshAnticipateBack(caller, from, to);
		modelMap.put("success", true);
		return modelMap;
	}
}
