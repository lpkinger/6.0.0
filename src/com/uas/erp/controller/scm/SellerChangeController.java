package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.SellerChangeService;

@Controller
public class SellerChangeController extends BaseController {
	@Autowired
	private SellerChangeService sellerChangeService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/saveSellerChange.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sellerChangeService.saveSellerChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/purchase/deleteSellerChange.action")  
	@ResponseBody 
	public Map<String, Object> delete(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sellerChangeService.deleteSellerChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updateSellerChange.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sellerChangeService.updateSellerChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitSellerChange.action")  
	@ResponseBody 
	public Map<String, Object> submitPurchase(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sellerChangeService.submitSellerChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitSellerChange.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sellerChangeService.resSubmitSellerChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditSellerChange.action")  
	@ResponseBody 
	public Map<String, Object> audit(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sellerChangeService.auditSellerChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
