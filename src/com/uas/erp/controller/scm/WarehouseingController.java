package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.WarehouseingService;

@Controller
public class WarehouseingController {
	@Autowired
	private WarehouseingService warehouseingService;

	/**
	 * 生成入仓单
	 */
	@RequestMapping("/scm/reserve/createWarehouseing.action")
	@ResponseBody
	public Map<String, Object> createWarehouseing(String whi_clientcode,String whi_clientname,int whi_amount,String whi_freefix,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String code=warehouseingService.createWarehouseing(whi_clientcode,whi_clientname,whi_amount,whi_freefix,caller);
		modelMap.put("success", true);
		modelMap.put("code", code);
		return modelMap;
	}

	/**
	 * 根据入仓单号查询日志
	 */
	@RequestMapping("/scm/reserve/getWarehouseingLog.action")
	@ResponseBody
	public Map<String, Object> getWarehouseingLog(String whi_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", warehouseingService.getWarehouseingLog(whi_code));
		return modelMap;
	}

	/**
	 * 更新入仓单
	 */
	@RequestMapping("/scm/reserve/updateWarehouseing.action")
	@ResponseBody
	public Map<String, Object> updateWarehouseing(String whi_code, String whi_status, String whi_text) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		warehouseingService.updateWarehouseing(whi_code, whi_status, whi_text);
		modelMap.put("success", true);
		return modelMap;
	}
}
