package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.SpeAttendanceService;

@Controller
public class SpeAttendanceController {
	@Autowired
	private SpeAttendanceService speattendanceService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/hr/attendance/saveSpeAttendance.action")
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request,String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		speattendanceService.saveSpeAttendance(formStore, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/hr/attendance/updateSpeAttendance.action")
	@ResponseBody
	public Map<String, Object> update(HttpServletRequest request,String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		speattendanceService.updateSpeAttendance(formStore, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/attendance/deleteSpeAttendance.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		speattendanceService.deleteSpeAttendance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核AskLeave
	 */
	@RequestMapping("/hr/attendance/auditSpeAttendance.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		speattendanceService.auditSpeAttendance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/attendance/resAuditSpeAttendance.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		speattendanceService.resAuditSpeAttendance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/attendance/submitSpeAttendance.action")
	@ResponseBody
	public Map<String, Object> submit(HttpServletRequest request,String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		speattendanceService.submitSpeAttendance(id, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/attendance/resSubmitSpeAttendance.action")
	@ResponseBody
	public Map<String, Object> resSubmit(HttpServletRequest request,String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		speattendanceService.resSubmitSpeAttendance(id, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 确认
	 */
	@RequestMapping("/hr/attendance/confirmSpeAttendance.action")
	@ResponseBody
	public Map<String, Object> confirmSpeAttendance(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		speattendanceService.confirmSpeAttendance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 结案
	 */
	@RequestMapping("/hr/attendance/endSpeAttendance.action")
	@ResponseBody
	public Map<String, Object> endSpeAttendance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		speattendanceService.endSpeAttendance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/hr/attendance/resEndSpeAttendance.action")
	@ResponseBody
	public Map<String, Object> resSpeAttendance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		speattendanceService.resEndSpeAttendance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
