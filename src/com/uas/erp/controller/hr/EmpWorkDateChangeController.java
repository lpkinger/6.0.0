package com.uas.erp.controller.hr;

import com.uas.erp.model.JSONTree;
import com.uas.erp.service.hr.EmpWorkDateChangeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class EmpWorkDateChangeController {

	@Autowired
	private EmpWorkDateChangeService empWorkDateChangeService;

	/**
	 * 保存
	 */
	@RequestMapping("/hr/attendance/saveEmpWorkDateChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateChangeService.saveEmpWorkDateChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/attendance/updateEmpWorkDateChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateChangeService.updateEmpWorkDateChangeById(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/attendance/deleteEmpWorkDateChange.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateChangeService.deleteEmpWorkDateChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 * @throws ParseException 
	 */
	@RequestMapping("hr/attendance/auditEmpWorkDateChange.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateChangeService.auditEmpWorkDateChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核
	 */
	@RequestMapping("hr/attendance/resAuditEmpWorkDateChange.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateChangeService.resAuditEmpWorkDateChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("hr/attendance/submitEmpWorkDateChange.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateChangeService.submitEmpWorkDateChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("hr/attendance/resSubmitEmpWorkDateChange.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateChangeService.resSubmitEmpWorkDateChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 返回日期范围内对应班次的员工
	 */
	@RequestMapping("hr/attendance/getEmp.action")
	@ResponseBody
	public Map<String, Object> getEmployees(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",empWorkDateChangeService.getEmployees(condition));
		modelMap.put("success", true);
		return modelMap;
	}
}
