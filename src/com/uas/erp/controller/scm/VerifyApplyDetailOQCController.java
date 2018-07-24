package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.VerifyApplyDetailOQCService;

@Controller
public class VerifyApplyDetailOQCController {
	@Autowired
	private VerifyApplyDetailOQCService VerifyApplyDetailOQCService;

	/**
	 * 保存VerifyApplyDetailOQC
	 */
	@RequestMapping("/scm/qc/saveVerifyApplyDetailOQC.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		VerifyApplyDetailOQCService.saveVerifyApplyDetailOQC(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/scm/qc/updateVerifyApplyDetailOQC.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		VerifyApplyDetailOQCService.updateVerifyApplyDetailOQCById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/scm/qc/deleteVerifyApplyDetailOQC.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		VerifyApplyDetailOQCService.deleteVerifyApplyDetailOQC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核VerifyApplyDetailOQC
	 */
	@RequestMapping("/scm/qc/auditVerifyApplyDetailOQC.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		VerifyApplyDetailOQCService.auditVerifyApplyDetailOQC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核VerifyApplyDetailOQC
	 */
	@RequestMapping("/scm/qc/resAuditVerifyApplyDetailOQC.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		VerifyApplyDetailOQCService.resAuditVerifyApplyDetailOQC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交VerifyApplyDetailOQC
	 */
	@RequestMapping("/scm/qc/submitVerifyApplyDetailOQC.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		VerifyApplyDetailOQCService.submitVerifyApplyDetailOQC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交VerifyApplyDetailOQC
	 */
	@RequestMapping("/scm/qc/resSubmitVerifyApplyDetailOQC.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		VerifyApplyDetailOQCService.resSubmitVerifyApplyDetailOQC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改PMC回复日期
	 * 
	 * @param pmc
	 *            回复日期
	 */
	@RequestMapping("/scm/qc/updatePMC.action")
	@ResponseBody
	public Map<String, Object> updateUU(String caller, Integer id, String pmc) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		VerifyApplyDetailOQCService.updatePMC(id, pmc, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
