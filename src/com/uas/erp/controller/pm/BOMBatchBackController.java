package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BOMBatchBackService;

@Controller
public class BOMBatchBackController extends BaseController {
	@Autowired
	private BOMBatchBackService BOMBatchBackService;

	/**
	 * 清除明细
	 */
	@RequestMapping("/pm/bom/cleanBOMBatchBack.action")
	@ResponseBody
	public Map<String, Object> cleanBOMBathExpand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMBatchBackService.cleanBOMBatchBack(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * bom批量多级反查
	 */
	@RequestMapping("/pm/bom/bomBack.action")
	@ResponseBody
	public Map<String, Object> bomBack(String caller, int id, String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMBatchBackService.bomBack(gridStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/bom/updateBOMBatchBack.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMBatchBackService.updateBOMBatchBackById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
