package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.scm.SaleForecastKindService;

@Controller
public class SaleForecastKindController {
	@Autowired
	private SaleForecastKindService saleForecastKindService;
	/**
	 * 保存SaleForecastKind
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/scm/sale/saveSaleForecastKind.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastKindService.saveSaleForecastKind(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/sale/updateSaleForecastKind.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastKindService.updateSaleForecastKindById(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteSaleForecastKind.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastKindService.deleteSaleForecastKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
