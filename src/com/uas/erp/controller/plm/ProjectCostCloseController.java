package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.ProjectCostCloseService;

@Controller
public class ProjectCostCloseController extends BaseController {
	@Autowired
	private ProjectCostCloseService projectCostCloseService;

	@RequestMapping("/plm/cost/saveProjectCostClose.action")
	@ResponseBody
	public Map<String, Object> saveProjectCostClose(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostCloseService.saveProjectCostClose(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/deleteProjectCostClose.action")
	@ResponseBody
	public Map<String, Object> deleteProjectCostClose(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostCloseService.deleteProjectCostClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/updateProjectCostClose.action")
	@ResponseBody
	public Map<String, Object> updateProjectCostClose(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostCloseService.updateProjectCostClose(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/submitProjectCostClose.action")
	@ResponseBody
	public Map<String, Object> submitProjectCostClose(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostCloseService.submitProjectCostClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/plm/cost/resSubmitProjectCostClose.action")
	@ResponseBody
	public Map<String, Object> resSubmitProjectCostClose(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostCloseService.resSubmitProjectCostClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/plm/cost/auditProjectCostClose.action")
	@ResponseBody
	public Map<String, Object> auditProjectCostClose(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostCloseService.auditProjectCostClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/plm/cost/resAuditProjectCostClose.action")
	@ResponseBody
	public Map<String, Object> resAuditProjectCostClose(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostCloseService.resAuditProjectCostClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 生成凭证
	 */
	@RequestMapping("/plm/cost/createCostVoucher.action")
	@ResponseBody
	public Map<String, Object> createCostVoucher(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", projectCostCloseService.createCostVoucher(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取消凭证
	 */
	@RequestMapping("/plm/cost/cancelCostVoucher.action")
	@ResponseBody
	public Map<String, Object> cancelCostVoucher(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostCloseService.cancelCostVoucher(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取项目
	 * 
	 * @param caller
	 * @param formStore
	 * @return
	 */
	@RequestMapping("/plm/cost/catchProjectCost.action")
	@ResponseBody
	public Map<String, Object> catchProjectCost(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostCloseService.catchProjectCost(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 清除项目
	 * 
	 * @param caller
	 * @param formStore
	 * @return
	 */
	@RequestMapping("/plm/cost/cleanProjectCost.action")
	@ResponseBody
	public Map<String, Object> cleanProjectCost(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostCloseService.cleanProjectCost(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}
}
