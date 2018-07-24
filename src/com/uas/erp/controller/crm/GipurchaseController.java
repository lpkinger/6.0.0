package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.GipurchaseService;

@Controller
public class GipurchaseController {
	@Autowired
	private GipurchaseService gipurchaseService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("crm/customercare/saveGipurchase.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		gipurchaseService.saveGipurchase(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("crm/customercare/updateGipurchase.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		gipurchaseService.updateGipurchaseById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("crm/customercare/deleteGipurchase.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		gipurchaseService.deleteGipurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("crm/customercare/submitGipurchase.action")
	@ResponseBody
	public Map<String, Object> submitGipurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		gipurchaseService.submitGipurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("crm/customercare/resSubmitGipurchase.action")
	@ResponseBody
	public Map<String, Object> resSubmitGipurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		gipurchaseService.resSubmitGipurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("crm/customercare/auditGipurchase.action")
	@ResponseBody
	public Map<String, Object> auditGipurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		gipurchaseService.auditGipurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("crm/customercare/resAuditGipurchase.action")
	@ResponseBody
	public Map<String, Object> resAuditGipurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		gipurchaseService.resAuditGipurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("crm/customercare/turnGiacceptance.action")
	@ResponseBody
	public Map<String, Object> turnOaacceptance(String formdata,
			String griddata, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		gipurchaseService.turnOaacceptance(formdata, griddata, caller);
		modelMap.put("success", true);
		return modelMap;

	}
}
