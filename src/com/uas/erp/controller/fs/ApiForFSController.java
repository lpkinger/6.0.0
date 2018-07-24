package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fs.ApiForFSService;

@Controller
public class ApiForFSController extends BaseController {

	@Autowired
	private ApiForFSService apiForFSService;
	
	/**
	 * 获取财务报表
	 */
	@RequestMapping("/openapi/factoring/getFaReports.action")
	@ResponseBody
	public Map<String, Object> getFaReports(String yearmonths, Boolean exitUDStream, Boolean right) {
		Map<String, Object> modelMap = apiForFSService.getFaReports(yearmonths, exitUDStream, right);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取项目风控报告默认值
	 */
	@RequestMapping("/openapi/factoring/getDefaultDataS.action")
	@ResponseBody
	public Map<String, Object> getDefaultDataS(Integer lastym, String applydate, Boolean financcondition, Boolean bankflow,
			Boolean productmix, Boolean updowncust, Boolean monetaryfund, Boolean accountinforar, Boolean accountinforothar,
			Boolean accountinforpp, Boolean accountinforinv, Boolean accountinforfix, Boolean accountinforlb, Boolean accountinforap,
			Boolean accountinforothap, Boolean accountinforlong) {
		Map<String, Object> modelMap = apiForFSService.getDefaultDataS(lastym, applydate, financcondition, bankflow, productmix,
				updowncust, monetaryfund, accountinforar, accountinforothar, accountinforpp, accountinforinv, accountinforfix,
				accountinforlb, accountinforap, accountinforothap, accountinforlong);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保理公司反写应收账款转让单
	 */
	@RequestMapping("/openapi/factoring/recBalanceAssign.action")
	@ResponseBody
	public Map<String, Object> recBalanceAssign(String mfcusts) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		apiForFSService.recBalanceAssign(mfcusts);
		modelMap.put("success", true);
		return modelMap;
	}

	
	/**
	 * 回写已使用额度
	 */
	@RequestMapping("/openapi/factoring/AccountApply.action")
	@ResponseBody
	public Map<String, Object> accountApply(String custcode, String custname, String amount, String cqcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apiForFSService.accountApply(custcode, custname, amount, cqcode);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取订单进度(客户)
	 */
	@RequestMapping("/openapi/factoring/getCustSaleReportProgress.action")  
	@ResponseBody 
	public Map<String, Object> getCustSaleReportProgress(String ordercode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", apiForFSService.getCustSaleReportProgress(ordercode));
		modelMap.put("success", true);
		return modelMap;
	}	
	
	/**
	 * 获取订单进度详情(客户)
	 */
	@RequestMapping("/openapi/factoring/getCustSaleReportDetail.action")  
	@ResponseBody 
	public Map<String, Object> getCustSaleReportDetail(String ordercode) {
		Map<String, Object> modelMap = apiForFSService.getCustSaleReportDetail(ordercode);
		modelMap.put("success", true);
		return modelMap;
	}	

}
