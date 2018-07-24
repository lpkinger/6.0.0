package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.OaacceptanceService;

@Controller
public class OaacceptanceController {

	@Autowired
	private OaacceptanceService oaacceptanceService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/appliance/saveOaacceptance.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaacceptanceService.saveOaacceptance(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/appliance/updateOaacceptance.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaacceptanceService.updateOaacceptanceById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/appliance/deleteOaacceptance.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaacceptanceService.deleteOaacceptance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/appliance/submitOaacceptance.action")
	@ResponseBody
	public Map<String, Object> submitOaacceptance(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaacceptanceService.submitOaacceptance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/appliance/resSubmitOaacceptance.action")
	@ResponseBody
	public Map<String, Object> resSubmitOaacceptance(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaacceptanceService.resSubmitOaacceptance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/appliance/auditOaacceptance.action")
	@ResponseBody
	public Map<String, Object> auditOaacceptance(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaacceptanceService.auditOaacceptance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/appliance/resAuditOaacceptance.action")
	@ResponseBody
	public Map<String, Object> resAuditOaacceptance(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaacceptanceService.resAuditOaacceptance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/* 过账 */
	@RequestMapping("/oa/appliance/turnOainstorage.action")
	@ResponseBody
	public Map<String, Object> turnOaacceptance(String caller, String formdata,
			String griddata) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		oaacceptanceService.turnOainstorage(formdata, griddata, caller);
		return modelMap;

	}

	/* 反过账 */
	@RequestMapping("/oa/appliance/returnOainstorage.action")
	@ResponseBody
	public Map<String, Object> returnOaacceptance(String caller,
			String formdata, String griddata) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		oaacceptanceService.returnOainstorage(formdata, griddata, caller);
		return modelMap;

	}

	/* 验退过账 */
	@RequestMapping("/oa/appliance/ytPost.action")
	@ResponseBody
	public Map<String, Object> ytPost(String caller, String formdata,
			String griddata) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		oaacceptanceService.ytPost(formdata, griddata, caller);
		return modelMap;

	}

	/* 验退反过账 */
	@RequestMapping("/oa/appliance/ytResPost.action")
	@ResponseBody
	public Map<String, Object> ytResPost(String caller, String formdata,
			String griddata) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		oaacceptanceService.ytResPost(formdata, griddata, caller);
		return modelMap;

	}

	@RequestMapping("/oa/appliance/printOaacceptance.action")
	@ResponseBody
	public Map<String, Object> printOaacceptance(String caller, int id,
			String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = oaacceptanceService.printOaacceptance(id, caller,
				reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/* 过账 */
	@RequestMapping("/oa/appliance/postOaacceptance.action")
	@ResponseBody
	public Map<String, Object> postOaacceptance(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		oaacceptanceService.postOaacceptance(id, caller);
		return modelMap;

	}

	/* 反过账 */
	@RequestMapping("/oa/appliance/resPostOaacceptance.action")
	@ResponseBody
	public Map<String, Object> resPostOaacceptance(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		oaacceptanceService.resPostOaacceptance(id, caller);
		return modelMap;

	}
}
