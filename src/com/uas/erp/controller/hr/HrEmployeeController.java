package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.hr.HrEmployeeService;

@Controller
public class HrEmployeeController extends BaseController {
	@Autowired
	private HrEmployeeService hrEmployeeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/hr/employee/saveEmployee.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrEmployeeService.saveEmployee(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除单据数据 包括采购明细
	 */
	@RequestMapping("/hr/employee/deleteEmployee.action")
	@ResponseBody
	public Map<String, Object> deleteEmployee(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrEmployeeService.deleteEmployee(id, caller);
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
	@RequestMapping("/hr/employee/updateEmployee.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrEmployeeService.updateEmployeeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印单据
	 */
	@RequestMapping("/hr/employee/printEmployee.action")
	@ResponseBody
	public Map<String, Object> printEmployee(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrEmployeeService.printEmployee(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交单据
	 */
	@RequestMapping("/hr/employee/submitEmployee.action")
	@ResponseBody
	public Map<String, Object> submitemployee(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrEmployeeService.submitEmployee(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交单据
	 */
	@RequestMapping("/hr/employee/resSubmitEmployee.action")
	@ResponseBody
	public Map<String, Object> resSubmitemployee(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrEmployeeService.resSubmitEmployee(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核单据
	 */
	@RequestMapping("/hr/employee/auditEmployee.action")
	@ResponseBody
	public Map<String, Object> auditemployee(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrEmployeeService.auditEmployee(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核单据
	 */
	@RequestMapping("/hr/employee/resAuditEmployee.action")
	@ResponseBody
	public Map<String, Object> resAuditemployee(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrEmployeeService.resAuditEmployee(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
