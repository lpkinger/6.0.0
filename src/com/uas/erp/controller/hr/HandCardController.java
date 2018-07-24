package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.HandCardService;

@Controller
public class HandCardController {

	@Autowired
	private HandCardService handCardService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/hr/attendance/saveHandCard.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		handCardService.saveHandCard(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/hr/attendance/updateHandCard.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		handCardService.updateHandCardById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/attendance/deleteHandCard.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		handCardService.deleteHandCard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/attendance/auditHandCard.action")
	@ResponseBody
	public Map<String, Object> auditHandCard(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		handCardService.auditHandCard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/attendance/insertEmployee.action")
	@ResponseBody
	public Map<String, Object> insertEmployee(String caller, int hcid,
			String deptcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		handCardService.insertEmployee(hcid, deptcode, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
