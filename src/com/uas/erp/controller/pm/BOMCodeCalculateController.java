package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BOMCodeCalculateService;

@Controller
public class BOMCodeCalculateController extends BaseController {
	@Autowired
	private BOMCodeCalculateService BOMCodeCalculateService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveBOMCodeCalculate.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCodeCalculateService.saveBOMCodeCalculate(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteBOMCodeCalculate.action")
	@ResponseBody
	public Map<String, Object> deleteBOMCodeCalculate(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCodeCalculateService.deleteBOMCodeCalculate(id, caller);
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
	@RequestMapping("/pm/bom/updateBOMCodeCalculate.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCodeCalculateService.updateBOMCodeCalculateById(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/bom/submitBOMCodeCalculate.action")
	@ResponseBody
	public Map<String, Object> submitBOMCodeCalculate(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCodeCalculateService.submitBOMCodeCalculate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/bom/resSubmitBOMCodeCalculate.action")
	@ResponseBody
	public Map<String, Object> resSubmitBOMCodeCalculate(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCodeCalculateService.resSubmitBOMCodeCalculate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/bom/auditBOMCodeCalculate.action")
	@ResponseBody
	public Map<String, Object> auditBOMCodeCalculate(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCodeCalculateService.auditBOMCodeCalculate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/bom/resAuditBOMCodeCalculate.action")
	@ResponseBody
	public Map<String, Object> resAuditBOMCodeCalculate(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCodeCalculateService.resAuditBOMCodeCalculate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
