package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BOMStepChangeService;

@Controller
public class BOMStepChangeController extends BaseController {
	@Autowired
	private BOMStepChangeService bomStepChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveBOMStepChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomStepChangeService.saveBOMStepChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteBOMStepChange.action")
	@ResponseBody
	public Map<String, Object> deleteBOMStepChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomStepChangeService.deleteBOMStepChange(id, caller);
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
	@RequestMapping("/pm/bom/updateBOMStepChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomStepChangeService.updateById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/bom/submitBOMStepChange.action")
	@ResponseBody
	public Map<String, Object> submitBOMStepChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomStepChangeService.submitBOMStepChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/bom/resSubmitBOMStepChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitBOMStepChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomStepChangeService.resSubmitBOMStepChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/bom/auditBOMStepChange.action")
	@ResponseBody
	public Map<String, Object> auditBOMStepChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomStepChangeService.auditBOMStepChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打开明细
	 */
	@RequestMapping("/pm/bom/BOMStepChangeOpenDet.action")
	@ResponseBody
	public Map<String, Object> BOMStepChangeOpenDet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomStepChangeService.BOMStepChangeOpenDet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 关闭明细
	 */
	@RequestMapping("/pm/bom/BOMStepChangeCloseDet.action")
	@ResponseBody
	public Map<String, Object> BOMStepChangeCloseDet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomStepChangeService.BOMStepChangeCloseDet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
