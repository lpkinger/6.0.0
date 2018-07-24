package com.uas.erp.controller.hr;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.SignCardService;

@Controller
public class SignCardController {
	@Autowired
	private SignCardService signCardService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 * @throws ParseException 
	 */
	@RequestMapping("/hr/attendance/saveSignCard.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		signCardService.saveSignCard(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 * @throws ParseException 
	 */
	@RequestMapping("/hr/attendance/updateSignCard.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		signCardService.updateSignCard(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/attendance/deleteSignCard.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
	    Map<String, Object> modelMap = new HashMap<String, Object>();
	    signCardService.deleteSignCard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/attendance/auditSignCard.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		signCardService.auditSignCard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核
	 */
	@RequestMapping("/hr/attendance/resAuditSignCard.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		signCardService.resAuditSignCard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/hr/attendance/submitSignCard.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		signCardService.submitSignCard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/attendance/resSubmitSignCard.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		signCardService.resSubmitSignCard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 结案
	 */
	@RequestMapping("/hr/attendance/endSignCard.action")
	@ResponseBody
	public Map<String, Object> endSignCard(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		signCardService.endSignCard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/hr/attendance/resEndSignCard.action")
	@ResponseBody
	public Map<String, Object> resEndSignCard(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		signCardService.resEndSignCard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
