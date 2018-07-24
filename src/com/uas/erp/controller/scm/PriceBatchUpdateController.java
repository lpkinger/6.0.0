package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.PriceBatchUpdateService;

@Controller
public class PriceBatchUpdateController extends BaseController {
	@Autowired
	private PriceBatchUpdateService priceBatchUpdateService;

	/**
	 * 清除失败的数据
	 */
	@RequestMapping("/scm/reserve/cleanFailed.action")
	@ResponseBody
	public Map<String, Object> cleanFailed(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceBatchUpdateService.cleanFailed(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/scm/reserve/deletePriceBatch.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceBatchUpdateService.delete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存
	 */
	@RequestMapping("/scm/reserve/savePriceBatch.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceBatchUpdateService.savePriceBatchById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/scm/reserve/updatePriceBatch.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceBatchUpdateService.updatePriceBatchById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量更新采购单价
	 */
	@RequestMapping("/scm/reserve/batchUpdateBill.action")
	@ResponseBody
	public Map<String, Object> batchUpdateBill(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceBatchUpdateService.batchUpdateBill(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 批量更新出货单价
	 */
	@RequestMapping("/scm/reserve/batchUpdateOutBill.action")
	@ResponseBody
	public Map<String, Object> batchUpdateOutBill(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceBatchUpdateService.batchUpdateOutBill(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/reserve/submitPriceBatch.action")
	@ResponseBody
	public Map<String, Object> submitPriceBatch(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceBatchUpdateService.submitPriceBatch(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/reserve/resSubmitPriceBatch.action")
	@ResponseBody
	public Map<String, Object> resSubmitPriceBatch(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceBatchUpdateService.resSubmitPriceBatch(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/reserve/auditPriceBatch.action")
	@ResponseBody
	public Map<String, Object> auditPriceBatch(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceBatchUpdateService.auditPriceBatch(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/reserve/resAuditPriceBatch.action")
	@ResponseBody
	public Map<String, Object> resAuditPriceBatch(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceBatchUpdateService.resAuditPriceBatch(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
}
