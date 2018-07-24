package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BOMDetailGroupReplaceService;

@Controller
public class BOMDetailGroupReplaceController extends BaseController {
	@Autowired
	private BOMDetailGroupReplaceService BOMDetailGroupReplaceService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveBOMDetailGroupReplace.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMDetailGroupReplaceService.saveBOMDetailGroupReplace(formStore,
				param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteBOMDetailGroupReplace.action")
	@ResponseBody
	public Map<String, Object> deleteBOMDetailGroupReplace(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMDetailGroupReplaceService.deleteBOMDetailGroupReplace(id, caller);
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
	@RequestMapping("/pm/bom/updateBOMDetailGroupReplace.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMDetailGroupReplaceService.updateBOMDetailGroupReplaceById(formStore,
				param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
