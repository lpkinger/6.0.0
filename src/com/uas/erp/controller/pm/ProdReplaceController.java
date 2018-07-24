package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ProdReplaceService;

@Controller
public class ProdReplaceController extends BaseController {
	@Autowired
	private ProdReplaceService prodReplaceService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveProdReplace.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceService.saveProdReplace(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteProdReplace.action")
	@ResponseBody
	public Map<String, Object> deleteProdReplace(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceService.deleteProdReplace(id, caller);
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
	@RequestMapping("/pm/bom/updateProdReplace.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceService.updateProdReplaceById(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 设为主料
	 */
	@RequestMapping("/pm/bom/setMain.action")
	@ResponseBody
	public Map<String, Object> setMain(String caller, int pre_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodReplaceService.setMain(pre_id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
