package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BOMKindService;

@Controller
public class BOMKindController extends BaseController {
	@Autowired
	private BOMKindService BOMKindService;

	/**
	 * 保存BOMKind
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/bom/saveBOMKind.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMKindService.saveBOMKind(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/bom/deleteBOMKind.action")
	@ResponseBody
	public Map<String, Object> deleteBOMKind(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMKindService.deleteBOMKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/bom/updateBOMKind.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMKindService.updateBOMKindById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
