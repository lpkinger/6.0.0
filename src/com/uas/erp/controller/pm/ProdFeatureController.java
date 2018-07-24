package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ProdFeatureService;

@Controller
public class ProdFeatureController extends BaseController {
	@Autowired
	private ProdFeatureService prodFeatureService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveProdFeature.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodFeatureService.saveProdFeature(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ECN数据 包括ECN明细
	 */
	@RequestMapping("/pm/bom/deleteProdFeature.action")
	@ResponseBody
	public Map<String, Object> deleteProdFeature(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodFeatureService.deleteProdFeature(id, caller);
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
	@RequestMapping("/pm/bom/updateProdFeature.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodFeatureService.updateProdFeatureById(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/feature/addProdFeature.action")
	@ResponseBody
	public Map<String, Object> addProdFeature(String caller, String formStore) {
		Map<String, Object> map = new HashMap<String, Object>();
		prodFeatureService.addProdFeature(formStore, caller);
		map.put("success", true);
		return map;

	}

}
