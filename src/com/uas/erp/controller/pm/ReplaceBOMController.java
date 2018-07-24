package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ReplaceBOMService;

@Controller
public class ReplaceBOMController extends BaseController {
	@Autowired
	private ReplaceBOMService replaceBOMService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveReplaceBOM.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		replaceBOMService.saveReplaceBOM(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteReplaceBOM.action")  
	@ResponseBody 
	public Map<String, Object> deleteReplaceBOM(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		replaceBOMService.deleteReplaceBOM(id ,caller);
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
	@RequestMapping("/pm/bom/updateReplaceBOM.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		replaceBOMService.updateReplaceBOMById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
