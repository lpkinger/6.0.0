package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.EmpWorkDateSpecialService;



@Controller
public class EmpWorkDateSpecialController {

	@Autowired
	private EmpWorkDateSpecialService empWorkDateSpecialService;

	/**
	 * 保存
	 */
	@RequestMapping("/hr/attendance/saveEmpWorkDateSpecial.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateSpecialService.saveEmpWorkDateSpecial(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/attendance/updateEmpWorkDateSpecial.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateSpecialService.updateEmpWorkDateSpecial(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/attendance/deleteEmpWorkDateSpecial.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateSpecialService.deleteEmpWorkDateSpecial(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("hr/attendance/submitEmpWorkDateSpecial.action")
	@ResponseBody
	public Map<String, Object> submitEmpWorkDateSpecial(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateSpecialService.submitEmpWorkDateSpecial(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("hr/attendance/resSubmitEmpWorkDateSpecial.action")
	@ResponseBody
	public Map<String, Object> resSubmitEmpWorkDateSpecial(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateSpecialService.resSubmitEmpWorkDateSpecial(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("hr/attendance/auditEmpWorkDateSpecial.action")
	@ResponseBody
	public Map<String, Object> auditEmpWorkDateSpecial(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateSpecialService.auditEmpWorkDateSpecial(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("hr/attendance/resAuditEmpWorkDateSpecial.action")
	@ResponseBody
	public Map<String, Object> resAuditEmpWorkDateSpecial(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateSpecialService.resAuditEmpWorkDateSpecial(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
