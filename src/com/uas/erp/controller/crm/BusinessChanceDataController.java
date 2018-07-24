package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.crm.BusinessChanceDataService;

@Controller
public class BusinessChanceDataController {
	@Autowired
	private BusinessChanceDataService BusinessChanceDataService;
	/**
	 * 获取登陆用户的代理商
	 */
	@RequestMapping("/crm/chance/getAgency.action")
	@ResponseBody
	public Map<String, Object> getAgency(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap=BusinessChanceDataService.getAgency(caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 保存
	 */
	@RequestMapping("/crm/chance/saveBusinessChanceData.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceDataService.saveBusinessChanceData(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/crm/chance/updateBusinessChanceData.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceDataService.updateBusinessChanceData(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/crm/chance/deleteBusinessChanceData.action")
	@ResponseBody
	public Map<String, Object> deleteBusinessChanceData(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceDataService.deleteBusinessChanceData(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/chance/submitBusinessChanceData.action")
	@ResponseBody
	public Map<String, Object> submitBusinessChanceData(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceDataService.submitBusinessChanceData(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/chance/resSubmitBusinessChanceData.action")
	@ResponseBody
	public Map<String, Object> resSubmitBusinessChanceData(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceDataService.resSubmitBusinessChanceData(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/chance/auditBusinessChanceData.action")
	@ResponseBody
	public Map<String, Object> auditBusinessChanceData(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceDataService.auditBusinessChanceData(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/chance/resAuditBusinessChanceData.action")
	@ResponseBody
	public Map<String, Object> resAuditBusinessChanceData(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceDataService.resAuditBusinessChanceData(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
