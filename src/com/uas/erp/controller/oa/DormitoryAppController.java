package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.DormitoryAppService;

@Controller
public class DormitoryAppController {
	@Autowired
	private DormitoryAppService dormitoryAppService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/publicAdmin/dormitory/Dormitory/saveDormitoryApp.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dormitoryAppService.saveDormitoryApp(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/oa/publicAdmin/dormitory/Dormitory/deleteDormitoryApp.action")
	@ResponseBody
	public Map<String, Object> deleteBook(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dormitoryAppService.deleteDormitoryApp(id, caller);
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
	@RequestMapping("/oa/publicAdmin/dormitory/Dormitory/updateDormitoryApp.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dormitoryAppService.updateDormitoryAppById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/publicAdmin/dormitory/Dormitory/submitDormitoryApp.action")
	@ResponseBody
	public Map<String, Object> submitDormitoryApp(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		dormitoryAppService.submitDormitoryApp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/publicAdmin/dormitory/Dormitory/resSubmitDormitoryApp.action")
	@ResponseBody
	public Map<String, Object> resSubmitDormitoryApp(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dormitoryAppService.resSubmitDormitoryApp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/publicAdmin/dormitory/Dormitory/auditDormitoryApp.action")
	@ResponseBody
	public Map<String, Object> auditDormitoryApp(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dormitoryAppService.auditDormitoryApp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/publicAdmin/dormitory/Dormitory/resAuditDormitoryApp.action")
	@ResponseBody
	public Map<String, Object> resAuditDormitoryApp(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dormitoryAppService.resAuditDormitoryApp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
