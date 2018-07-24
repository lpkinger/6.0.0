package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.model.Employee;
import com.uas.erp.service.hr.ExtraWorkService;

@Controller
public class ExtraWorkController {
	@Autowired
	private ExtraWorkService extraWorkService;
	
	
	@RequestMapping("/hr/attendance/saveExtraWork.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session,String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		extraWorkService.saveExtraWork(session,formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	//加班申请单保存和提交
		@RequestMapping("/mobile/oa/ExtraWorkSaveAndSubmit.action")  
		@ResponseBody 
		public Map<String,Object> ExtraWorkSaveAndSubmit(HttpServletRequest request,HttpSession session,String caller,String formStore){
			Employee employee=(Employee)request.getSession().getAttribute("employee");
			if(employee==null) BaseUtil.showError("会话已断开!");
			Map<String, Object> modelMap = new HashMap<String, Object>();
			modelMap=extraWorkService.ExtraWorkSaveAndSubmit(session,caller,formStore,employee);
			modelMap.put("success", true);
			return modelMap;
		}
		//加班申请单更新和提交
				@RequestMapping("/mobile/oa/ExtraWorkUpdateAndSubmit.action")  
				@ResponseBody 
				public Map<String,Object> ExtraWorkUpdateAndSubmit(HttpServletRequest request,String caller,String formStore){
					Employee employee=(Employee)request.getSession().getAttribute("employee");
					if(employee==null) BaseUtil.showError("会话已断开!");
					Map<String, Object> modelMap = new HashMap<String, Object>();
					extraWorkService.ExtraWorkUpdateAndSubmit(caller,formStore);
					modelMap.put("success", true);
					return modelMap;
				}
	/**
	 * 修改
	 */
	@RequestMapping("/hr/attendance/updateExtraWork.action")
	@ResponseBody
	public Map<String, Object> update(HttpServletRequest request,String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		extraWorkService.updateExtraWork(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/hr/attendance/deleteExtraWork.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		extraWorkService.deleteExtraWork(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/hr/attendance/submitExtraWork.action")
	@ResponseBody
	public Map<String, Object> submitExtraWork(HttpServletRequest request,String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		extraWorkService.submitExtraWork(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/hr/attendance/resSubmitExtraWork.action")
	@ResponseBody
	public Map<String, Object> resSubmitExtraWork(HttpServletRequest request,String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		extraWorkService.resSubmitExtraWork(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/hr/attendance/auditExtraWork.action")
	@ResponseBody
	public Map<String, Object> auditExtraWork(HttpSession session,String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		extraWorkService.auditExtraWork(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/attendance/resAuditExtraWork.action")
	@ResponseBody
	public Map<String, Object> resAuditExtraWork(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		extraWorkService.resAuditExtraWork(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
