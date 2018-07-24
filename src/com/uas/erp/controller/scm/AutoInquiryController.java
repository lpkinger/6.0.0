package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.AutoInquiryService;

@Controller
public class AutoInquiryController  extends BaseController {
	@Autowired
	private AutoInquiryService autoInquiryService;
	
	@RequestMapping("/scm/purchase/saveAutoInquiry.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		autoInquiryService.saveAutoInquiry(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/scm/purchase/getAutoInquiry.action")  
	@ResponseBody 
	public Map<String, Object> getGridStore(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", autoInquiryService.getGridStore());
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/purchase/deleteAutoInquiry.action")  
	@ResponseBody 
	public Map<String, Object> deleteAcceptNotify(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		autoInquiryService.deleteAutoInquiry(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updateAutoInquiry.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param,String caller,String sign) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		autoInquiryService.updateAutoInquiry(formStore,param, caller,sign);
		modelMap.put("success", true);
		return modelMap;
	}
	/*
	 * 自动询价-按具体物料批量更新
	 */
	@RequestMapping("/scm/purchase/updateInquiryProd.action")  
	@ResponseBody 
	public Map<String, Object> updateInquiryProd(String data,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = autoInquiryService.updateInquiryProd(data, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
	/*
	 * 自动询价-按具体物料批量更新
	 */
	@RequestMapping("/scm/purchase/inquiryTurnPrice.action")  
	@ResponseBody 
	public Map<String, Object> inquiryTurnPrice(String data,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = autoInquiryService.inquiryTurnPrice(data, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
	
}
