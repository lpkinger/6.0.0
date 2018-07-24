package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BOMDetailLocationService;

@Controller
public class BOMDetailLocationController extends BaseController {
	@Autowired
	private BOMDetailLocationService BOMDetailLocationService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveBOMDetailLocation.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String param,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMDetailLocationService
				.saveBOMDetailLocation(param, caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteBOMDetailLocation.action")
	@ResponseBody
	public Map<String, Object> deleteBOMDetailLocation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMDetailLocationService.deleteBOMDetailLocation(id, caller);
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
	@RequestMapping("/pm/bom/updateBOMDetailLocation.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String param,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMDetailLocationService.updateBOMDetailLocationById(param, caller,
				formStore);
		modelMap.put("success", true);
		return modelMap;
	}

}
