package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.VePaymentsService;

@Controller
public class VePaymentsController extends BaseController {
	@Autowired
	private VePaymentsService vePaymentsService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/purchase/saveVePayments.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vePaymentsService.saveVePayments(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/purchase/deleteVePayments.action")
	@ResponseBody
	public Map<String, Object> deleteVePayments(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vePaymentsService.deleteVePayments(id, caller);
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
	@RequestMapping("/scm/purchase/updateVePayments.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vePaymentsService.updateVePaymentsById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updateVendorBank.action")  
	@ResponseBody 
	public Map<String, Object> updateVendorBank(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vePaymentsService.updateVendorBankById(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

}
