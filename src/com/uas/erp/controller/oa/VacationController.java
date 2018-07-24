package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.VacationService;

@Controller
public class VacationController {

	@Autowired
	private VacationService vacationService;

	/**
	 * 保存oaOrg
	 */
	@RequestMapping("/oa/check/saveVacation.action")
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request,String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vacationService.saveVacation(formStore, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/oa/check/updateVacation.action")
	@ResponseBody
	public Map<String, Object> update(HttpServletRequest request,String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vacationService.updateVacationById(formStore, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/check/deleteVacation.action")
	@ResponseBody
	public Map<String, Object> delete(HttpServletRequest request,String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vacationService.deleteVacation(id, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap; 
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/check/submitVacation.action")
	@ResponseBody
	public Map<String, Object> submitVacation(HttpServletRequest request,String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vacationService.submitVacation(id, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/check/resSubmitVacation.action")
	@ResponseBody
	public Map<String, Object> resSubmitVacation(HttpServletRequest request,String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vacationService.resSubmitVacation(id, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/check/auditVacation.action")
	@ResponseBody
	public Map<String, Object> auditVacation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vacationService.auditVacation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/check/resAuditVacation.action")
	@ResponseBody
	public Map<String, Object> resAuditVacation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vacationService.resAuditVacation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核Ask4Leave
	 */
	@RequestMapping("/oa/check/auditAsk4Leave.action")
	@ResponseBody
	public Map<String, Object> auditAsk4Leave(String caller, int id,
			String auditstatus) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vacationService.auditAsk4Leave(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核Ask4Leave
	 */
	@RequestMapping("/oa/check/resAuditAsk4Leave.action")
	@ResponseBody
	public Map<String, Object> resAuditAsk4Leave(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vacationService.resAuditAsk4Leave(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确认Ask4Leave
	 */
	@RequestMapping("/oa/check/confirmVacation.action")
	@ResponseBody
	public Map<String, Object> confirmAsk4Leave(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vacationService.confirmAsk4Leave(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 勾选参数:病假提交检查累计病假天数,提交前检查
	 * 请假类型为病假检查天数
	 */
	@RequestMapping("/oa/check/sickCheck.action")  
	@ResponseBody 
	public Map<String, Object> sickCheck(String caller, int id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap=vacationService.sickCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 清除年假
	 */
	@RequestMapping("/oa/check/cleanEmpdays.action")  
	@ResponseBody 
	public Map<String, Object> cleanEmpdays(String caller, int id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vacationService.cleanEmpdays(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反结案
	 */
	@RequestMapping("/oa/check/resEndVacation.action")
	@ResponseBody
	public Map<String, Object> resEndVacation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vacationService.resEndVacation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 结案
	 */
	@RequestMapping("/oa/check/endVacation.action")
	@ResponseBody
	public Map<String, Object> endVacation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vacationService.endVacation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
