package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.TurnBomPleaseService;

@Controller
public class TurnBomPleaseController extends BaseController {
	@Autowired
	private TurnBomPleaseService turnBomPleaseService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveTurnBomPlease.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnBomPleaseService.saveTurnBomPlease(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteTurnBomPlease.action")
	@ResponseBody
	public Map<String, Object> deleteMakeChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnBomPleaseService.deleteTurnBomPlease(caller, id);
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
	@RequestMapping("/pm/bom/updateTurnBomPlease.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnBomPleaseService.updateTurnBomPleaseById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/bom/submitTurnBomPlease.action")
	@ResponseBody
	public Map<String, Object> submitMakeChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnBomPleaseService.submitTurnBomPlease(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/bom/resSubmitTurnBomPlease.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnBomPleaseService.resSubmitTurnBomPlease(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/bom/auditTurnBomPlease.action")
	@ResponseBody
	public Map<String, Object> auditMakeChange(String caller, int id
			) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnBomPleaseService.auditTurnBomPlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/bom/resAuditTurnBomPlease.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnBomPleaseService.resAuditTurnBomPlease(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转标准BOM
	 * */
	@RequestMapping("/pm/bom/turnStandard.action")
	@ResponseBody
	public Map<String, Object> turnStandard(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnBomPleaseService.turnStandard(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

}
