package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ECRService;

@Controller
public class ECRController extends BaseController {
	@Autowired
	private ECRService ECRService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveECR.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRService.saveECR(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteECR.action")
	@ResponseBody
	public Map<String, Object> deleteECR(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRService.deleteECR(id, caller);
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
	@RequestMapping("/pm/bom/updateECR.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRService.updateECRById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/bom/submitECR.action")
	@ResponseBody
	public Map<String, Object> submitECR(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRService.submitECR(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/bom/resSubmitECR.action")
	@ResponseBody
	public Map<String, Object> resSubmitECR(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRService.resSubmitECR(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/bom/auditECR.action")
	@ResponseBody
	public Map<String, Object> auditECR(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRService.auditECR(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/bom/resAuditECR.action")
	@ResponseBody
	public Map<String, Object> resAuditECR(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRService.resAuditECR(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
