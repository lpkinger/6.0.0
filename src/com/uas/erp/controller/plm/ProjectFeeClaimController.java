package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.ProjectFeeClaimService;

@Controller
public class ProjectFeeClaimController extends BaseController {
	@Autowired
	private ProjectFeeClaimService projectFeeClaimService;

	@RequestMapping("/plm/cost/saveProjectFeeClaim.action")
	@ResponseBody
	public Map<String, Object> saveProjectFeeClaim(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFeeClaimService.saveProjectFeeClaim(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/deleteProjectFeeClaim.action")
	@ResponseBody
	public Map<String, Object> deleteProjectFeeClaim(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFeeClaimService.deleteProjectFeeClaim(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/updateProjectFeeClaim.action")
	@ResponseBody
	public Map<String, Object> updateProjectFeeClaim(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFeeClaimService.updateProjectFeeClaim(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/submitProjectFeeClaim.action")
	@ResponseBody
	public Map<String, Object> submitProjectFeeClaim(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFeeClaimService.submitProjectFeeClaim(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/plm/cost/resSubmitProjectFeeClaim.action")
	@ResponseBody
	public Map<String, Object> resSubmitProjectFeeClaim(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFeeClaimService.resSubmitProjectFeeClaim(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/plm/cost/auditProjectFeeClaim.action")
	@ResponseBody
	public Map<String, Object> auditProjectFeeClaim(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFeeClaimService.auditProjectFeeClaim(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/plm/cost/resAuditProjectFeeClaim.action")
	@ResponseBody
	public Map<String, Object> resAuditProjectFeeClaim(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFeeClaimService.resAuditProjectFeeClaim(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
