package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeChangeService;

@Controller
public class MakeChangeController extends BaseController {
	@Autowired
	private MakeChangeService makeChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/saveMakeChange.action")
	@ResponseBody
	public Map<String, Object> save(String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeChangeService.saveMakeChange(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/make/deleteMakeChange.action")
	@ResponseBody
	public Map<String, Object> deleteMakeChange(int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeChangeService.deleteMakeChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/updateMakeChange.action")
	@ResponseBody
	public Map<String, Object> update(String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeChangeService
				.updateMakeChangeById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/make/submitMakeChange.action")
	@ResponseBody
	public Map<String, Object> submitMakeChange(int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeChangeService.submitMakeChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/make/resSubmitMakeChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeChange(int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeChangeService.resSubmitMakeChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/make/auditMakeChange.action")
	@ResponseBody
	public Map<String, Object> auditMakeChange(int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeChangeService.auditMakeChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/make/resAuditMakeChange.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeChange(int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeChangeService.resAuditMakeChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
}
