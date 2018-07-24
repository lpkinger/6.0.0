package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BOMSetChangeService;

@Controller
public class BOMSetChangeController extends BaseController {
	@Autowired
	private BOMSetChangeService BomSetChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveBOMSetChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BomSetChangeService.saveBOMSetChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteBOMSetChange.action")
	@ResponseBody
	public Map<String, Object> deleteBOMSetChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BomSetChangeService.deleteBOMSetChange(id, caller);
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
	@RequestMapping("/pm/bom/updateBOMSetChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BomSetChangeService.updateById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/bom/submitBOMSetChange.action")
	@ResponseBody
	public Map<String, Object> submitBOMSetChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BomSetChangeService.submitBOMSetChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/bom/resSubmitBOMSetChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitBOMSetChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BomSetChangeService.resSubmitBOMSetChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/bom/auditBOMSetChange.action")
	@ResponseBody
	public Map<String, Object> auditBOMSetChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BomSetChangeService.auditBOMSetChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
