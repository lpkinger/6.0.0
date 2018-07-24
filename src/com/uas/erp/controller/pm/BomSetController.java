package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BomSetService;

@Controller
public class BomSetController extends BaseController {
	@Autowired
	private BomSetService BomSetService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveBomSet.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BomSetService.saveBomSet(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteBomSet.action")
	@ResponseBody
	public Map<String, Object> deleteBomSet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BomSetService.deleteBomSet(id, caller);
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
	@RequestMapping("/pm/bom/updateBomSet.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BomSetService.updateBomSetById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/bom/submitBomSet.action")
	@ResponseBody
	public Map<String, Object> submitBomSet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BomSetService.submitBomSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/bom/resSubmitBomSet.action")
	@ResponseBody
	public Map<String, Object> resSubmitBomSet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BomSetService.resSubmitBomSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/bom/auditBomSet.action")
	@ResponseBody
	public Map<String, Object> auditBomSet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BomSetService.auditBomSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/bom/resAuditBomSet.action")
	@ResponseBody
	public Map<String, Object> resAuditBomSet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BomSetService.resAuditBomSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
