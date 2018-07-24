package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.oa.CarAllowanceService;

@Controller
public class CarAllowanceController {

	@Autowired
	private CarAllowanceService carAllowanceService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/fee/saveCarAllowance.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		carAllowanceService.saveCarAllowance(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/fee/updateCarAllowance.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		carAllowanceService.updateCarAllowance(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/fee/deleteCarAllowance.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		carAllowanceService.deleteCarAllowance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/fee/resAuditCarAllowance.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		carAllowanceService.resAuditCarAllowance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/fee/submitCarAllowance.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		carAllowanceService.submitCarAllowance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/fee/resSubmitCarAllowance.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		carAllowanceService.resSubmitCarAllowance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/fee/auditCarAllowance.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		carAllowanceService.auditCarAllowance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 确认CarAllowance
	 */
	@RequestMapping("/oa/fee/confirmCarAllowance.action")  
	@ResponseBody 
	public Map<String, Object> confirmCarAllowance(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		carAllowanceService.confirmCarAllowance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
