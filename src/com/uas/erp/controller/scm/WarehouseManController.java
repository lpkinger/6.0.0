package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.WarehouseManService;

@Controller
public class WarehouseManController extends BaseController {
	@Autowired
	private WarehouseManService warehouseManService;
	
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/reserve/updateWarehouseMan.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		warehouseManService.updateWarehouseManById(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/scm/reserve/clearWareMan.action")  
	@ResponseBody 
	public Map<String, Object> clearWareMan(String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		warehouseManService.clearWareMan(caller, condition);
		modelMap.put("success", true);
		return modelMap;
	}
}
