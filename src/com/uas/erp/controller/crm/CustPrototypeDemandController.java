package com.uas.erp.controller.crm;

import com.uas.opensys.service.PrototypeDemandService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CustPrototypeDemandController {

	@Autowired
	private PrototypeDemandService prototypeDemandService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/crm/aftersalemgr/savePrototypeDemand.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prototypeDemandService.savePrototypeDemand(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/crm/aftersalemgr/updatePrototypeDemand.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prototypeDemandService.updatePrototypeDemandById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/crm/aftersalemgr/deletePrototypeDemand.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prototypeDemandService.deletePrototypeDemand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/aftersalemgr/submitPrototypeDemand.action")
	@ResponseBody
	public Map<String, Object> submitPrototypeDemand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prototypeDemandService.submitPrototypeDemand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/aftersalemgr/resSubmitPrototypeDemand.action")
	@ResponseBody
	public Map<String, Object> resSubmitPrototypeDemand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prototypeDemandService.resSubmitPrototypeDemand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/aftersalemgr/auditPrototypeDemand.action")
	@ResponseBody
	public Map<String, Object> auditPrototypeDemand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prototypeDemandService.auditPrototypeDemand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/aftersalemgr/resAuditPrototypeDemand.action")
	@ResponseBody
	public Map<String, Object> resAuditPrototypeDemand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prototypeDemandService.resAuditPrototypeDemand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
