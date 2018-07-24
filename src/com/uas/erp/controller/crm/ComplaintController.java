package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.ComplaintService;

@Controller
public class ComplaintController {
	@Autowired
	private ComplaintService complaintService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("crm/aftersalemgr/saveComplaint.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintService.saveComplaint(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 
	 * 
	 */
	@RequestMapping("crm/aftersalemgr/deleteComplaint.action")
	@ResponseBody
	public Map<String, Object> deleteChance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintService.deleteComplaint(id, caller);
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
	@RequestMapping("crm/aftersalemgr/updateComplaint.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintService.updateComplaint(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("crm/aftersalemgr/submitComplaint.action")
	@ResponseBody
	public Map<String, Object> submitComplaint(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintService.submitComplaint(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("crm/aftersalemgr/resSubmitComplaint.action")
	@ResponseBody
	public Map<String, Object> resSubmitComplaint(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintService.resSubmitComplaint(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("crm/aftersalemgr/auditComplaint.action")
	@ResponseBody
	public Map<String, Object> auditComplaint(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintService.auditComplaint(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("crm/aftersalemgr/resAuditComplaint.action")
	@ResponseBody
	public Map<String, Object> resAuditComplaint(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintService.resAuditComplaint(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
