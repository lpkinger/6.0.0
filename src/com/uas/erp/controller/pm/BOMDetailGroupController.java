package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BOMDetailGroupService;

@Controller
public class BOMDetailGroupController extends BaseController {
	@Autowired
	private BOMDetailGroupService BOMDetailGroupService;

	/**
	 * 保存WorkTime
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/bom/saveBOMDetailGroup.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMDetailGroupService.saveBOMDetailGroup(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/bom/deleteBOMDetailGroup.action")
	@ResponseBody
	public Map<String, Object> deleteBOMDetailGroup(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMDetailGroupService.deleteBOMDetailGroup(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/bom/updateBOMDetailGroup.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMDetailGroupService.updateBOMDetailGroupById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
