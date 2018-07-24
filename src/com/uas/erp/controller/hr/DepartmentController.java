package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.DepartmentnewService;

@Controller
public class DepartmentController {
	@Autowired
	private DepartmentnewService departmentService;

	/**
	 *部门
	 */
	@RequestMapping("/hr/employee/saveDepartment.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
    		String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		departmentService.saveDepartment(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/employee/updateDepartment.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		departmentService.updateDepartmentById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/employee/deleteDepartment.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		departmentService.deleteDepartment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 获取部门信息
	 * */
	@RequestMapping("/hr/employee/getDepartments.action")
	@ResponseBody
	public Map<String, Object> getDepartments() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("departs", departmentService.getDepartments());
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/hr/employee/auditDepartment.action")
	@ResponseBody
	public Map<String, Object> auditDepartment( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		departmentService.auditDepartment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/hr/employee/resAuditDepartment.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		departmentService.resAuditDepartment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
