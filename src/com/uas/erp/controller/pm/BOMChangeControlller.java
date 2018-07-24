package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BOMChangeService;

@Controller
public class BOMChangeControlller extends BaseController {
	@Autowired
	private BOMChangeService BOMChangeService;

	/**
	 * 保存BOMChange
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/craft/saveBOM.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMChangeService.saveBOM(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/craft/deleteBOM.action")
	@ResponseBody
	public Map<String, Object> deleteMakeCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMChangeService.deleteBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/craft/updateBOM.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMChangeService.updateBOMById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交作业资料
	 */
	@RequestMapping("/pm/craft/submitBOM.action")
	@ResponseBody
	public Map<String, Object> submitMakeCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMChangeService.submitBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交作业资料
	 */
	@RequestMapping("/pm/craft/resSubmitBOM.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMChangeService.resSubmitBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核作业资料
	 */
	@RequestMapping("/pm/craft/auditBOM.action")
	@ResponseBody
	public Map<String, Object> auditMakeCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMChangeService.auditBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核作业资料
	 */
	@RequestMapping("/pm/craft/resAuditBOM.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMChangeService.resAuditBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
