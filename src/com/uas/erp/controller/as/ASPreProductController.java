package com.uas.erp.controller.as;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.model.Page;
import com.uas.erp.service.as.PreProductService;
import com.uas.erp.service.ma.ConfigService;

@Controller
public class ASPreProductController {

	@Autowired
	private PreProductService preProductService;

	@Autowired
	private ConfigService configService;

	@RequestMapping("/as/port/getPreProduct.action")
	@ResponseBody
	public Map<String, Object> getPreProduct(String filters) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String, Object> filterMap = FlexJsonUtil.fromJson(filters);
		modelMap.put("data", preProductService.getPreProduct(filterMap));
		return modelMap;
	}
	
	@RequestMapping("/as/port/getPreProductDetail.action")
	@ResponseBody
	public Map<String, Object> getPreProductDetail(HttpSession session, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", preProductService.getPreProductDetail(code));
		return modelMap;
	}
	
	/**
	 * 新物料申请单批量转出库单
	 */
	@RequestMapping(value = "/as/port/applyToProdIO.action")
	@ResponseBody
	public Map<String, Object> applyToProdIO(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", preProductService.applyToProdIO(data));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 新物料申请单批量删除
	 */
	@RequestMapping(value = "/as/port/applyDelete.action")
	@ResponseBody
	public Map<String, Object> applyDelete(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", preProductService.applyDelete(data));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/as/port/getApplyList.action")
	@ResponseBody
	public Page<Map<String, Object>> getApplyList(int page, int start, int limit, String filters) {
		Map<String, Object> filterMap = FlexJsonUtil.fromJson(filters);
		return preProductService.getApplyList(page, start, limit, filterMap);
	}
}
