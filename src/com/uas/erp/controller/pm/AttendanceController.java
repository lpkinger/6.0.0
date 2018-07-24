package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.AttendanceService;

@Controller
public class AttendanceController extends BaseController {
	@Autowired
	private AttendanceService attendanceService;

	@RequestMapping("/pm/make/copyAttendance.action")
	@ResponseBody
	public Map<String, Object> copy(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int atid = attendanceService.copyAttendance(id,caller);
		modelMap.put("id", atid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存
	 */
	@RequestMapping("/pm/make/saveAttendance.action")
	@ResponseBody
	public Map<String, Object> save(String formStore,String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendanceService.saveAttendance(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/pm/make/updateAttendance.action")
	@ResponseBody
	public Map<String, Object> update(String formStore,String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendanceService.updateAttendance(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteAttendance.action")
	@ResponseBody
	public Map<String, Object> delete(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendanceService.deleteAttendance(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitAttendance.action")
	@ResponseBody
	public Map<String, Object> submitLossWorkTime(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendanceService.submitAttendance(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitAttendance.action")
	@ResponseBody
	public Map<String, Object> resSubmitLossWorkTime(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendanceService.resSubmitAttendance( id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditAttendance.action")
	@ResponseBody
	public Map<String, Object> auditLossWorkTime(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendanceService.auditAttendance( id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditAttendance.action")
	@ResponseBody
	public Map<String, Object> resAuditLossWorkTime(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendanceService.resAuditAttendance( id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
