package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.AllotApplicationCuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AllotApplicationCuController {

	@Autowired
	private AllotApplicationCuService allotApplicationCuService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/distribution/saveAllotApplicationCu.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		allotApplicationCuService.saveAllotApplicationCu(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/distribution/updateAllotApplicationCu.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		allotApplicationCuService.updateAllotApplicationCuById(formStore,
				param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/distribution/deleteAllotApplicationCu.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		allotApplicationCuService.deleteAllotApplicationCu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/distribution/submitAllotApplicationCu.action")
	@ResponseBody
	public Map<String, Object> submitAllotApplicationCu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		allotApplicationCuService.submitAllotApplicationCu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/distribution/resSubmitAllotApplicationCu.action")
	@ResponseBody
	public Map<String, Object> resSubmitAllotApplicationCu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		allotApplicationCuService.resSubmitAllotApplicationCu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/distribution/auditAllotApplicationCu.action")
	@ResponseBody
	public Map<String, Object> auditAllotApplicationCu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		allotApplicationCuService.auditAllotApplicationCu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/distribution/resAuditAllotApplicationCu.action")
	@ResponseBody
	public Map<String, Object> resAuditAllotApplicationCu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		allotApplicationCuService.resAuditAllotApplicationCu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
