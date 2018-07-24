package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ECNDOCService;

@Controller
public class ECNDOCController extends BaseController {
	@Autowired
	private ECNDOCService ECNDOCService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveECNDOC.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNDOCService.saveECNDOC(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteECNDOC.action")
	@ResponseBody
	public Map<String, Object> deleteECNDOC(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNDOCService.deleteECNDOC(id, caller);
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
	@RequestMapping("/pm/bom/updateECNDOC.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNDOCService.updateECNDOCById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/bom/submitECNDOC.action")
	@ResponseBody
	public Map<String, Object> submitECNDOC(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNDOCService.submitECNDOC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/bom/resSubmitECNDOC.action")
	@ResponseBody
	public Map<String, Object> resSubmitECNDOC(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNDOCService.resSubmitECNDOC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/bom/auditECNDOC.action")
	@ResponseBody
	public Map<String, Object> auditECNDOC(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNDOCService.auditECNDOC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/bom/resAuditECNDOC.action")
	@ResponseBody
	public Map<String, Object> resAuditECNDOC(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNDOCService.resAuditECNDOC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
