package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.DevelopBOMService;

@Controller
public class DevelopBOMController extends BaseController {
	@Autowired
	private DevelopBOMService developBOMService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveDevelopBOM.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		developBOMService.saveDevelopBOM(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteDevelopBOM.action")
	@ResponseBody
	public Map<String, Object> deleteDevelopBOM(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		developBOMService.deleteDevelopBOM(id, caller);
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
	@RequestMapping("/pm/bom/updateDevelopBOM.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		developBOMService.updateDevelopBOMById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/bom/submitDevelopBOM.action")
	@ResponseBody
	public Map<String, Object> submitDevelopBOM(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		developBOMService.submitDevelopBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/bom/resSubmitDevelopBOM.action")
	@ResponseBody
	public Map<String, Object> resSubmitDevelopBOM(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		developBOMService.resSubmitDevelopBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/bom/auditDevelopBOM.action")
	@ResponseBody
	public Map<String, Object> auditDevelopBOM(String caller, int id) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
		developBOMService.auditDevelopBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/bom/resAuditDevelopBOM.action")
	@ResponseBody
	public Map<String, Object> resAuditDevelopBOM(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		developBOMService.resAuditDevelopBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
