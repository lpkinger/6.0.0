package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.StockTakingService;

@Controller
public class StockTakingController extends BaseController {
	@Autowired
	private StockTakingService stockTakingService;
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/reserve/deleteStockTaking.action")  
	@ResponseBody 
	public Map<String, Object> deleteStockTaking(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stockTakingService.deleteStockTaking(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/reserve/updateStockTaking.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stockTakingService.updateStockTakingById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/reserve/auditStockTaking.action")  
	@ResponseBody 
	public Map<String, Object> auditStockTaking(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stockTakingService.auditStockTaking(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/reserve/resAuditStockTaking.action")  
	@ResponseBody 
	public Map<String, Object> resAuditStockTaking(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stockTakingService.resAuditStockTaking(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
