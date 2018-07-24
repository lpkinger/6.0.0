package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BOMDetailDocService;

@Controller
public class BOMDetailDocController extends BaseController {
	@Autowired
	private BOMDetailDocService BOMDetailDocService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveBOMDetailDoc.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMDetailDocService.saveBOMDetailDoc(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteBOMDetailDoc.action")
	@ResponseBody
	public Map<String, Object> deleteBOMDetailDoc(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMDetailDocService.deleteBOMDetailDoc(id, caller);
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
	@RequestMapping("/pm/bom/updateBOMDetailDoc.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMDetailDocService.updateBOMDetailDocById(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
