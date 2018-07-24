package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.AttendapplyService;

@Controller
public class AttendapplyController {

	@Autowired
	private AttendapplyService attendapplyService;

	/**
	 * 保存oaOrg
	 */
	@RequestMapping("/oa/check/saveAttendapply.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendapplyService.saveAttendapply(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/oa/check/updateAttendapply.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendapplyService.updateAttendapplyById(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/check/deleteAttendapply.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendapplyService.deleteAttendapply(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/check/submitAttendapply.action")
	@ResponseBody
	public Map<String, Object> submitAttendapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendapplyService.submitAttendapply(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/check/resSubmitAttendapply.action")
	@ResponseBody
	public Map<String, Object> resSubmitAttendapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendapplyService.resSubmitAttendapply(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/check/auditAttendapply.action")
	@ResponseBody
	public Map<String, Object> auditAttendapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendapplyService.auditAttendapply(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/check/resAuditAttendapply.action")
	@ResponseBody
	public Map<String, Object> resAuditAttendapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendapplyService.resAuditAttendapply(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
