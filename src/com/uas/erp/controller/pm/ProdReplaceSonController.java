package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ProdReplaceSonService;

@Controller
public class ProdReplaceSonController extends BaseController {
	@Autowired
	private ProdReplaceSonService prodReplaceSonService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveProdReplaceSon.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceSonService.saveProdReplaceSon(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteProdReplaceSon.action")
	@ResponseBody
	public Map<String, Object> deleteProdReplaceSon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceSonService.deleteProdReplaceSon(id, caller);
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
	@RequestMapping("/pm/bom/updateProdReplaceSon.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceSonService
				.updateProdReplaceSonById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/bom/submitProdReplaceSon.action")
	@ResponseBody
	public Map<String, Object> submitProdReplaceSon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceSonService.submitProdReplaceSon(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/bom/resSubmitProdReplaceSon.action")
	@ResponseBody
	public Map<String, Object> resSubmitProdReplaceSon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceSonService.resSubmitProdReplaceSon(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/bom/auditProdReplaceSon.action")
	@ResponseBody
	public Map<String, Object> auditProdReplaceSon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceSonService.auditProdReplaceSon(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/bom/resAuditProdReplaceSon.action")
	@ResponseBody
	public Map<String, Object> resAuditProdReplaceSon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceSonService.resAuditProdReplaceSon(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
