package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.crm.SellerSaleReportService;

@Controller
public class SellerSaleReportController extends BaseController {
	@Autowired
	private SellerSaleReportService sellerSaleReportService;

	@RequestMapping("/crm/customermgr/saveSellerSaleReport.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		sellerSaleReportService.saveSellerSaleReport(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/crm/marketmgr/deleteSellerSaleReport.action")
	@ResponseBody
	public Map<String, Object> deleteMarketProject(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		sellerSaleReportService.deleteSellerSaleReport(id, caller);
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
	@RequestMapping("/crm/marketmgr/updateSellerSaleReport.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		sellerSaleReportService
				.updateSellerSaleReport(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
