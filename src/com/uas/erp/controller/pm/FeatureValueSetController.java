package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.FeatureValueSetService;

@Controller
public class FeatureValueSetController extends BaseController {
	@Autowired
	private FeatureValueSetService featureValueSetService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/getDescription.action")
	@ResponseBody
	public Map<String, Object> getDescription(String caller, String tablename,
			String field, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("description", featureValueSetService
				.getDataFieldByCondition(tablename, field, condition, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/bom/updateDescription.action")
	@ResponseBody
	public Map<String, Object> updateDescription(String caller,
			String tablename, String[] field, String[] fieldvalue,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureValueSetService.updateDataFieldByCondition(tablename, field,
				fieldvalue, condition, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/bom/getRealCode.action")
	@ResponseBody
	public Map<String, Object> getRealCode(String caller, String prodcode,
			String specdescription, String fromwhere) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("realCode", featureValueSetService.getRealCode(prodcode,
				specdescription, fromwhere, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/getFields.action")
	@ResponseBody
	public Map<String, Object> getFields(String caller, String tablename,
			String[] field, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", featureValueSetService.getDataFieldsByCondition(
				tablename, field, condition, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
