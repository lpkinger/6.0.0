package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.VisitRecord3Service;

@Controller
public class VisitRecord3Controller {
	@Autowired
	private VisitRecord3Service visitRecord3Service;

	/**
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/crm/customermgr/saveVisitRecord3.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param1,
			String param2, String param3, String param4, String param5,
			String param6, String param7, String param8, String param9,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { param1, param2, param3, param4,
				param5, param6, param7, param8, param9 };
		visitRecord3Service.saveVisitRecord(formStore, params, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/crm/customermgr/deleteVisitRecord3.action")
	@ResponseBody
	public Map<String, Object> deleteVisitRecord(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitRecord3Service.deleteVisitRecord(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/crm/customermgr/updateVisitRecord3.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param1,
			String param2, String param3, String param4, String param5,
			String param6, String param7, String param8, String param9,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { param1, param2, param3, param4,
				param5, param6, param7, param8, param9 };
		visitRecord3Service.updateVisitRecordById(formStore, params, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/customermgr/submitVisitRecord3.action")
	@ResponseBody
	public Map<String, Object> submitVisitRecord(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitRecord3Service.submitVisitRecord(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/customermgr/resSubmitVisitRecord3.action")
	@ResponseBody
	public Map<String, Object> resSubmitVisitRecord(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitRecord3Service.resSubmitVisitRecord(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/customermgr/auditVisitRecord3.action")
	@ResponseBody
	public Map<String, Object> auditVisitRecord(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitRecord3Service.auditVisitRecord(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/customermgr/resAuditVisitRecord3.action")
	@ResponseBody
	public Map<String, Object> resAuditVisitRecord(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitRecord3Service.resAuditVisitRecord(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据填写的客户和录入人，获取符合条件的最新记录。再一次插入数据库中，简化了录入工作
	 */
	@RequestMapping("/crm/customermgr/autoSaveVisitRecord3.action")
	@ResponseBody
	public Map<String, Object> autoSave(String vr_cuuu, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int vr_id = visitRecord3Service.autoSave(caller, vr_cuuu);
		modelMap.put("success", true);
		modelMap.put("vr_id", vr_id);
		return modelMap;
	}

	/**
	 * 转差旅报销申请
	 */
	@RequestMapping("/crm/customermgr/turnFeePlease3.action")
	@ResponseBody
	public Map<String, Object> turnFeePlease(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = visitRecord3Service.turnFeePlease(id, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
}
