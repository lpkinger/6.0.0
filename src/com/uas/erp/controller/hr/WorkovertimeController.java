package com.uas.erp.controller.hr;

import com.uas.erp.service.hr.WorkovertimeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@Controller
public class WorkovertimeController {

	@Autowired
	private WorkovertimeService workovertimeService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/hr/attendance/saveWorkovertime.action")
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request,String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workovertimeService.saveWorkovertime(formStore, param, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/hr/attendance/updateWorkovertime.action")
	@ResponseBody
	public Map<String, Object> update(HttpServletRequest request,String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workovertimeService.updateWorkovertimeById(formStore, param, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/attendance/deleteWorkovertime.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workovertimeService.deleteWorkovertime(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/attendance/submitWorkovertime.action")
	@ResponseBody
	public Map<String, Object> submitWorkovertime(HttpServletRequest request,String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workovertimeService.submitWorkovertime(id, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/attendance/resSubmitWorkovertime.action")
	@ResponseBody
	public Map<String, Object> resSubmitWorkovertime(HttpServletRequest request,String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workovertimeService.resSubmitWorkovertime(id, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/attendance/auditWorkovertime.action")
	@ResponseBody
	public Map<String, Object> auditWorkovertime(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workovertimeService.auditWorkovertime(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/attendance/resAuditWorkovertime.action")
	@ResponseBody
	public Map<String, Object> resAuditWorkovertime(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workovertimeService.resAuditWorkovertime(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 天派加班申请同步到东宝系统
	 */
	@RequestMapping("/hr/attendance/turndbWorkovertime.action")
	@ResponseBody
	public Map<String, Object> syncPurc(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workovertimeService.syncDB(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确认申请
	 */
	@RequestMapping("/hr/attendance/confirmWorkovertime.action")
	@ResponseBody
	public Map<String, Object> confirmWorkovertime(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workovertimeService.confirmWorkovertime(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
