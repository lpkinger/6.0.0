package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.PreProdFeatureService;

@Controller
public class PreProdFeatureController extends BaseController {
	@Autowired
	private PreProdFeatureService preProdFeatureService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/savePreProdFeature.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProdFeatureService.savePreProdFeature(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/bom/deletePreProdFeature.action")
	@ResponseBody
	public Map<String, Object> deletePreProdFeature(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProdFeatureService.deletePreProdFeature(id, caller);
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
	@RequestMapping("/pm/bom/updatePreProdFeature.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProdFeatureService.updatePreProdFeatureById(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
