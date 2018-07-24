package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BOMTestService;

@Controller
public class BOMTestController extends BaseController {
	@Autowired
	private BOMTestService BOMTestService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveBOMTest.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMTestService.saveBOMTest(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteBOMTest.action")
	@ResponseBody
	public Map<String, Object> deleteBOMTest(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMTestService.deleteBOMTest(id, caller);
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
	@RequestMapping("/pm/bom/updateBOMTest.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMTestService.updateBOMTestById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/bom/submitBOMTest.action")
	@ResponseBody
	public Map<String, Object> submitBOMTest(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMTestService.submitBOMTest(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/bom/resSubmitBOMTest.action")
	@ResponseBody
	public Map<String, Object> resSubmitBOMTest(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMTestService.resSubmitBOMTest(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/bom/auditBOMTest.action")
	@ResponseBody
	public Map<String, Object> auditBOMTest(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMTestService.auditBOMTest(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/bom/resAuditBOMTest.action")
	@ResponseBody
	public Map<String, Object> resAuditBOMTest(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMTestService.resAuditBOMTest(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
