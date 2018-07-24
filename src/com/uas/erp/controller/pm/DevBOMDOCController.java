package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.DevBOMDOCService;

@Controller
public class DevBOMDOCController extends BaseController {
	@Autowired
	private DevBOMDOCService devBOMDOCService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveDevBOMDOC.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		devBOMDOCService.saveDevBOMDOC(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteDevBOMDOC.action")
	@ResponseBody
	public Map<String, Object> deleteDevBOMDOC(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		devBOMDOCService.deleteDevBOMDOC(id, caller);
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
	@RequestMapping("/pm/bom/updateDevBOMDOC.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		devBOMDOCService.updateDevBOMDOCById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
