package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeScrapmakeService;

@Controller
public class MakeScrapmakeController extends BaseController {

	@Autowired
	private MakeScrapmakeService makeScrapmakeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/saveMakeScrapmake.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeScrapmakeService.saveMakeScrapmake(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/make/deleteMakeScrapmake.action")
	@ResponseBody
	public Map<String, Object> deleteMakeScrap(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeScrapmakeService.deleteMakeScrapmake(id, caller);
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
	@RequestMapping("/pm/make/updateMakeScrapmake.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeScrapmakeService.updateMakeScrapmakeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/make/submitMakeScrapmake.action")
	@ResponseBody
	public Map<String, Object> submitMakeScrap(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeScrapmakeService.submitMakeScrapmake(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/make/resSubmitMakeScrapmake.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeScrap(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeScrapmakeService.resSubmitMakeScrapmake(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/make/auditMakeScrapmake.action")
	@ResponseBody
	public Map<String, Object> auditMakeScrap(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeScrapmakeService.auditMakeScrapmake(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/make/resAuditMakeScrapmake.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeScrap(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeScrapmakeService.resAuditMakeScrapmake(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
