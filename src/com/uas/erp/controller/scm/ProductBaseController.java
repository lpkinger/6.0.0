package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductBaseService;

@Controller
public class ProductBaseController {
	@Autowired
	private ProductBaseService productBaseService;

	/**
	 * 保存product!base
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/scm/product/saveProductBase.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBaseService.saveProductBase(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/scm/product/updateProductBase.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBaseService.updateProductBaseById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deleteProductBase.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBaseService.deleteProductBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitProductBase.action")
	@ResponseBody
	public Map<String, Object> submitProductBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBaseService.submitProductBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitProductBase.action")
	@ResponseBody
	public Map<String, Object> resSubmitProductBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBaseService.resSubmitProductBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/product/auditProductBase.action")
	@ResponseBody
	public Map<String, Object> auditProductBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBaseService.auditProductBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditProductBase.action")
	@ResponseBody
	public Map<String, Object> resAuditProductBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBaseService.resAuditProductBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 物料赋值
	 */
	@RequestMapping("/scm/product/copyProduct.action")
	@ResponseBody
	public Map<String, Object> copyProduct(String caller, int id, String newcode, String newname, String newspec ) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int prid = productBaseService.copyProduct(id, caller, newcode, newname, newspec);
		modelMap.put("id", prid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 易方转标准 非标准
	 * */
	@RequestMapping("/scm/product/commitStandard.action")
	@ResponseBody
	public Map<String, Object> SubmitStandard(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBaseService.SubmitStandard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/scm/product/resSubmitNoStandard.action")
	@ResponseBody
	public Map<String, Object> resSubmitStandard(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBaseService.resSubmitNoStandard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 客户物料维护
	 */
	@RequestMapping("/scm/product/saveCustProd.action")
	@ResponseBody
	public Map<String, Object> saveCustprod(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBaseService.saveCustprod(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
