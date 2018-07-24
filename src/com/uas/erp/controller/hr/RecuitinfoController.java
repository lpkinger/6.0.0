package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.RecuitinfoService;

@Controller
public class RecuitinfoController {

	@Autowired
	private RecuitinfoService recuitinfoService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveRecuitinfo.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recuitinfoService.saveRecuitinfo(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateRecuitinfo.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recuitinfoService.updateRecuitinfoById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteRecuitinfo.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recuitinfoService.deleteRecuitinfo(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/hr/emplmana/submitRecuitinfo.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recuitinfoService.submitRecuitinfo(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitRecuitinfo.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recuitinfoService.resSubmitRecuitinfo(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditRecuitinfo.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recuitinfoService.auditRecuitinfo(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditRecuitinfo.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recuitinfoService.resAuditRecuitinfo(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量转笔试
	 */
	@RequestMapping(value = "/hr/emplmana/vastWriexam.action")
	@ResponseBody
	public Map<String, Object> vastWriteexam(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recuitinfoService.vastWriteexam(caller, id);
		modelMap.put("success", true);
		return modelMap;

	}

	/**
	 * 批量转面试
	 */
	@RequestMapping(value = "/hr/emplmana/vastInterview.action")
	@ResponseBody
	public Map<String, Object> vastinterview(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recuitinfoService.vastinterview(caller, id);
		modelMap.put("success", true);
		return modelMap;

	}

	/**
	 * 批量转人才库（列表界面）
	 */
	@RequestMapping(value = "/hr/emplmana/vastJointalcpool.action")
	@ResponseBody
	public Map<String, Object> vastJointalcpool(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recuitinfoService.vastJointalcpool(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 批量转人才库（批量处理界面）
	 */
	@RequestMapping(value = "/hr/vastTurnJointalcpool.action")
	@ResponseBody
	public Map<String, Object> vastTurnJointalcpool(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", recuitinfoService.vastTurnJointalcpool(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量录入笔试成绩
	 */
	@RequestMapping(value = "/hr/emplmana/vastWritemark.action")
	@ResponseBody
	public Map<String, Object> vastWritemark(String caller, int[] id, int[] mark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recuitinfoService.vastWritemark(caller, id, mark);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量录入面试成绩
	 */
	@RequestMapping(value = "/hr/emplmana/vastIntermark.action")
	@ResponseBody
	public Map<String, Object> vastIntermark(String caller, int[] id, int[] mark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recuitinfoService.vastInterviewmark(caller, id, mark);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转录用申请单（列表界面）
	 */
	@RequestMapping(value = "/hr/emplmana/vastTurnCaree.action")
	@ResponseBody
	public Map<String, Object> vastTurnrecruitplan(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recuitinfoService.vastTurnrecruitplan(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转录用申请单（批量处理界面）
	 */
	@RequestMapping(value = "/hr/vastTurnCaree.action")
	@ResponseBody
	public Map<String, Object> vastTurnrecruitplan(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", recuitinfoService.vastTurnrecruitplan(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}
}
