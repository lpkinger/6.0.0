package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.WarehouseService;

@Controller
public class WarehouseController extends BaseController {
	@Autowired
	WarehouseService warehouseService;

	/**
	 * @author wsy 保存仓库资料
	 */
	@RequestMapping("/scm/saveWarehouse.action")
	@ResponseBody
	public Map<String, Object> saveWarehouse(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		warehouseService.saveWarehouse(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * @author wsy 删除仓库资料
	 */
	@RequestMapping("/scm/deleteWarehouse.action")
	@ResponseBody
	public Map<String, Object> deleteWarehouse(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		warehouseService.deleteWarehouse(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * @author wsy 更新仓库资料
	 */
	@RequestMapping("/scm/updateWarehouse.action")
	@ResponseBody
	public Map<String, Object> updateWarehouse(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		warehouseService.updateWarehouse(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * @author madan 审核Warehouse
	 */
	@RequestMapping("/scm/auditWarehouse.action")
	@ResponseBody
	public Map<String, Object> audit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		warehouseService.auditWarehouse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核Warehouse
	 */
	@RequestMapping("/scm/resAuditWarehouse.action")
	@ResponseBody
	public Map<String, Object> resAudit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		warehouseService.resAuditWarehouse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交Warehouse
	 */
	@RequestMapping("/scm/submitWarehouse.action")
	@ResponseBody
	public Map<String, Object> submit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		warehouseService.submitWarehouse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交Warehouse
	 */
	@RequestMapping("/scm/resSubmitWarehouse.action")
	@ResponseBody
	public Map<String, Object> resSubmit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		warehouseService.resSubmitWarehouse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取有效Warehouse
	 */
	@RequestMapping("/scm/getWarehouse.action")
	@ResponseBody
	public Map<String, Object> getWarehouse() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", warehouseService.getWarehouse());
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新Warehouse——IsMallStore
	 */
	@RequestMapping("/scm/updateIsMallStore.action")
	@ResponseBody
	public Map<String, Object> updateIsMallStore(String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//warehouseService.updateIsMallStore(param);
		modelMap.put("success", true);
		modelMap.put("log", warehouseService.updateIsMallStore(param));
		return modelMap;
	}
}
