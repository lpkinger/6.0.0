package com.uas.erp.controller.fs;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.SaleReportService;


@Controller
public class SaleReportController {
	
	@Autowired
	private SaleReportService saleReportService;
	
	/**
	 * 获取订单进度
	 */
	@RequestMapping("/fs/cust/getSaleReportProgress.action")  
	@ResponseBody 
	public Map<String, Object> getSaleReportProgress(String custcode, String ordercode) {
		Map<String, Object> modelMap = saleReportService.getSaleReportData(custcode, ordercode, "/openapi/factoring/getCustSaleReportProgress.action");
		modelMap.put("success", true);
		return modelMap;
	}	
	
	/**
	 * 获取订单进度详情
	 */
	@RequestMapping("/fs/cust/getSaleReportDetail.action")  
	@ResponseBody 
	public Map<String, Object> getSaleReportDetail(String custcode, String ordercode) {
		Map<String, Object> modelMap = saleReportService.getSaleReportData(custcode, ordercode, "/openapi/factoring/getCustSaleReportDetail.action");
		modelMap.put("success", true);
		return modelMap;
	}	

}
