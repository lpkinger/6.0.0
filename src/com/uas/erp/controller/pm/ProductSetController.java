package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ProductSetService;

@Controller
public class ProductSetController extends BaseController {
	@Autowired
	private ProductSetService productSetService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mould/saveProductSet.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSetService.saveProductSet(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/mould/deleteProductSet.action")
	@ResponseBody
	public Map<String, Object> deleteProductSet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSetService.deleteProductSet(id, caller);
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
	@RequestMapping("/pm/mould/updateProductSet.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSetService.updateProductSetById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/pm/mould/printProductSet.action")
	@ResponseBody
	public Map<String, Object> printProductSet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSetService.printProductSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mould/submitProductSet.action")
	@ResponseBody
	public Map<String, Object> submitProductSet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSetService.submitProductSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mould/resSubmitProductSet.action")
	@ResponseBody
	public Map<String, Object> resSubmitProductSet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSetService.resSubmitProductSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mould/auditProductSet.action")
	@ResponseBody
	public Map<String, Object> auditProductSet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSetService.auditProductSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mould/resAuditProductSet.action")
	@ResponseBody
	public Map<String, Object> resAuditProductSet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSetService.resAuditProductSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新返还数量
	 * */
	@RequestMapping("/pm/mould/updateReturnqty.action")
	@ResponseBody
	public Map<String, Object> updateReturnqty(HttpSession session, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSetService.updateReturnqty(data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/pm/mould/updateVendReturn.action")  
	@ResponseBody
	public Map<String, Object> updateVendReturn(HttpSession session, Integer id, String returnstatus, String returnremark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSetService.updateVendReturn(id, returnstatus, returnremark);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/pm/mould/updateCustReturn.action")  
	@ResponseBody
	public Map<String, Object> updateCustReturn(HttpSession session, Integer id, String returnstatus, String returnremark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSetService.updateCustReturn(id, returnstatus, returnremark);
		modelMap.put("success", true);
		return modelMap;
	}
}
