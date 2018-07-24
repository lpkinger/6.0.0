package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ProdReplaceMotherService;

@Controller
public class ProdReplaceMotherController extends BaseController {
	@Autowired
	private ProdReplaceMotherService prodReplaceMotherService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveProdReplaceMother.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceMotherService
				.saveProdReplaceMother(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteProdReplaceMother.action")
	@ResponseBody
	public Map<String, Object> deleteProdReplaceMother(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceMotherService.deleteProdReplaceMother(id, caller);
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
	@RequestMapping("/pm/bom/updateProdReplaceMother.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceMotherService.updateProdReplaceMotherById(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/bom/submitProdReplaceMother.action")
	@ResponseBody
	public Map<String, Object> submitProdReplaceMother(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceMotherService.submitProdReplaceMother(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/bom/resSubmitProdReplaceMother.action")
	@ResponseBody
	public Map<String, Object> resSubmitProdReplaceMother(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceMotherService.resSubmitProdReplaceMother(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/bom/auditProdReplaceMother.action")
	@ResponseBody
	public Map<String, Object> auditProdReplaceMother(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceMotherService.auditProdReplaceMother(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/bom/resAuditProdReplaceMother.action")
	@ResponseBody
	public Map<String, Object> resAuditProdReplaceMother(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceMotherService.resAuditProdReplaceMother(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
