package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BOMSonService;

@Controller
public class BOMSonController extends BaseController {
	@Autowired
	private BOMSonService BOMSonService;

	/**
	 * 保存BOMDetail
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/bom/saveBOMSon.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMSonService.saveBOMSon(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/bom/deleteBOMSon.action")
	@ResponseBody
	public Map<String, Object> deleteBOMDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMSonService.deleteBOMSon(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/bom/updateBOMSon.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMSonService.updateBOMSonById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
