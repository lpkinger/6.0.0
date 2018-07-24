package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductWarehouseService;

@Controller
public class ProductWarehouseController {
	@Autowired
	private ProductWarehouseService productWarehouseService;
	/**
	 * 保存product!Warehouse
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/scm/reserve/saveProductWarehouse.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productWarehouseService.saveProductWarehouse(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/reserve/updateProductWarehouse.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productWarehouseService.updateProductWarehouseById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
}
