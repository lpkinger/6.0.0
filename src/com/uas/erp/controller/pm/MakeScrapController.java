package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeScrapService;

@Controller
public class MakeScrapController extends BaseController {

	@Autowired
	private MakeScrapService makeScrapService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/saveMakeScrap.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeScrapService.saveMakeScrap(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/make/deleteMakeScrap.action")
	@ResponseBody
	public Map<String, Object> deleteMakeScrap(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeScrapService.deleteMakeScrap(id, caller);
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
	@RequestMapping("/pm/make/updateMakeScrap.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeScrapService.updateMakeScrapById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/make/submitMakeScrap.action")
	@ResponseBody
	public Map<String, Object> submitMakeScrap(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeScrapService.submitMakeScrap(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/make/resSubmitMakeScrap.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeScrap(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeScrapService.resSubmitMakeScrap(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/make/auditMakeScrap.action")
	@ResponseBody
	public Map<String, Object> auditMakeScrap(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeScrapService.auditMakeScrap(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/make/resAuditMakeScrap.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeScrap(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeScrapService.resAuditMakeScrap(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印报废单
	 */
	@RequestMapping("/pm/make/printMakeScrap.action")
	@ResponseBody
	public Map<String, Object> print(String caller, int id, String reportName,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = makeScrapService.printMakeScrap(id, caller, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
}
