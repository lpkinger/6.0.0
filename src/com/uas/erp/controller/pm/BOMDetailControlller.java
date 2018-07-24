package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BOMDetailService;

@Controller
public class BOMDetailControlller extends BaseController {
	@Autowired
	private BOMDetailService BOMDetailService;

	/**
	 * 保存BOMDetail
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/bom/saveBOMDetail.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMDetailService.saveBOMDetail(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/bom/deleteBOMDetail.action")
	@ResponseBody
	public Map<String, Object> deleteBOMDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMDetailService.deleteBOMDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/bom/updateBOMDetail.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMDetailService.updateBOMDetailById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
