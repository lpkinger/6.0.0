package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BOMSonSeqencingService;

@Controller
public class BOMSonSeqencingController extends BaseController {
	@Autowired
	private BOMSonSeqencingService BOMSonSeqencingService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveBOMSonSeqencing.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMSonSeqencingService.saveBOMSonSeqencing(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteBOMSonSeqencing.action")
	@ResponseBody
	public Map<String, Object> deleteBOMSonSeqencing(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMSonSeqencingService.deleteBOMSonSeqencing(id, caller);
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
	@RequestMapping("/pm/bom/updateBOMSonSeqencing.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMSonSeqencingService.updateBOMSonSeqencingById(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/bom/submitBOMSonSeqencing.action")
	@ResponseBody
	public Map<String, Object> submitBOMSonSeqencing(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMSonSeqencingService.submitBOMSonSeqencing(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/bom/resSubmitBOMSonSeqencing.action")
	@ResponseBody
	public Map<String, Object> resSubmitBOMSonSeqencing(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMSonSeqencingService.resSubmitBOMSonSeqencing(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/bom/auditBOMSonSeqencing.action")
	@ResponseBody
	public Map<String, Object> auditBOMSonSeqencing(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMSonSeqencingService.auditBOMSonSeqencing(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/bom/resAuditBOMSonSeqencing.action")
	@ResponseBody
	public Map<String, Object> resAuditBOMSonSeqencing(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMSonSeqencingService.resAuditBOMSonSeqencing(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
