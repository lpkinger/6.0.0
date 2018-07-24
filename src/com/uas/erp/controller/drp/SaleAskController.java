package com.uas.erp.controller.drp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;

import com.uas.erp.service.drp.SaleAskService;

@Controller
public class SaleAskController extends BaseController {
	@Autowired
	private SaleAskService saleAskService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/drp/distribution/saveSaleAsk.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleAskService.saveSaleAsk(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 */
	@RequestMapping("/drp/distribution/deleteSaleAsk.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleAskService.deleteSaleAsk(id, caller);
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
	@RequestMapping("/drp/distribution/updateSaleAsk.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleAskService.updateSaleAsk(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/distribution/submitSaleAsk.action")
	@ResponseBody
	public Map<String, Object> submitPurchase(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleAskService.submitSaleAsk(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交销售单
	 */
	@RequestMapping("/drp/distribution/resSubmitSaleAsk.action")
	@ResponseBody
	public Map<String, Object> resSubmit(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleAskService.resSubmitSaleAsk(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核销售单
	 */
	@RequestMapping("/drp/distribution/auditSaleAsk.action")
	@ResponseBody
	public Map<String, Object> audit(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleAskService.auditSaleAsk(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核销售单
	 */
	@RequestMapping("/drp/distribution/resAuditSaleAsk.action")
	@ResponseBody
	public Map<String, Object> resAudit(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleAskService.resAuditSaleAsk(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印销售单
	 */
	@RequestMapping("/drp/distribution/printSaleAsk.action")
	@ResponseBody
	public Map<String, Object> print(int id, String reportName,
			String condition, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = saleAskService.printSaleAsk(id, caller, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 结案
	 */
	@RequestMapping("/drp/distribution/endSaleAsk.action")
	@ResponseBody
	public Map<String, Object> endSaleAsk(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleAskService.endSaleAsk(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/drp/distribution/resEndSaleAsk.action")
	@ResponseBody
	public Map<String, Object> resEndSaleAsk(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleAskService.resEndSaleAsk(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转发货通知单
	 */
	@RequestMapping("/drp/distribution/saleAskturnNotify.action")
	@ResponseBody
	public Map<String, Object> turnSendNotify(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		int snid = saleAskService.turnSendNotify(id, caller);
		modelMap.put("id", snid);
		modelMap.put("success", true);
		return modelMap;
	}
}
