package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.GiacceptanceService;

@Controller
public class GiacceptanceController {
	@Autowired
	private GiacceptanceService giacceptanceService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/crm/customercare/saveGiacceptance.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giacceptanceService.saveGiacceptance(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/crm/customercare/updateGiacceptance.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giacceptanceService.updateGiacceptanceById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/crm/customercare/deleteGiacceptance.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giacceptanceService.deleteGiacceptance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/customercare/submitGiacceptance.action")
	@ResponseBody
	public Map<String, Object> submitOaacceptance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giacceptanceService.submitGiacceptance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/customercare/resSubmitGiacceptance.action")
	@ResponseBody
	public Map<String, Object> resSubmitOaacceptance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giacceptanceService.resSubmitGiacceptance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/customercare/auditGiacceptance.action")
	@ResponseBody
	public Map<String, Object> auditOaacceptance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giacceptanceService.auditGiacceptance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/customercare/resAuditGiacceptance.action")
	@ResponseBody
	public Map<String, Object> resAuditOaacceptance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giacceptanceService.resAuditGiacceptance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/crm/customercare/turninstorage.action")
	@ResponseBody
	public Map<String, Object> turnOaacceptance(String formdata,
			String griddata, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		giacceptanceService.turnOainstorage(formdata, griddata, caller);
		return modelMap;

	}
}
