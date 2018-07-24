package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.scm.ProductKindService;

@Controller
public class ProductKindController {
	@Autowired
	private ProductKindService productKindService;

	/**
	 * 保存ProductKind
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/scm/sale/saveProductKind.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productKindService.saveProductKind(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/scm/sale/updateProductKind.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productKindService.updateProductKindById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteProductKind.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productKindService.deleteProductKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据父节点加载子节点
	 */
	@RequestMapping(value = "/scm/product/getProductKindTree.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(String caller, int parentid, String allKind) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 此处Employee在el表达式用到，不能去掉
		List<JSONTree> tree = productKindService.getJsonTrees(SystemSession.getUser(), parentid, allKind, caller);
		modelMap.put("tree", tree);
		return modelMap;
	}
	
	/**
	 * 按搜索条件加载树
	 */
	@RequestMapping(value = "/scm/product/searchProductKindTree.action")
	@ResponseBody
	public Map<String, Object> getTreeBySearch(HttpSession session, String search) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		List<JSONTree> tree = productKindService.getJSONTreeBySearch(search, employee);
		modelMap.put("tree", tree);
		return modelMap;
	}

	/**
	 * 失效和转有效
	 */
	@RequestMapping(value = "/scm/product/effective.action")
	@ResponseBody
	public Map<String, Object> setEffective(String caller, int id, Boolean bool) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productKindService.setEffective(id, bool);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据{id}取对应的编号
	 */
	@RequestMapping(value = "/scm/product/getProductKindNum.action")
	@ResponseBody
	public Map<String, Object> getProductKindNum(Integer id, String k1, String k2, String k3, String k4 ,String postfix) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (id != null)
			modelMap.put("number", productKindService.getProductKindNum(id,postfix));
		else
			modelMap.put("number", productKindService.getProductKindNumByKind(k1, k2, k3, k4,postfix));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新物料损耗率
	 */
	@RequestMapping("/scm/sale/updateProdLoss.action")
	@ResponseBody
	public Map<String, Object> updateProdLoss(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productKindService.updateProdLoss(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/sale/addProductKindByParent.action")
	@ResponseBody
	public Map<String, Object> addProductKindByParent( int parentId) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("pkind", productKindService.addProductKindByParent(parentId));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取物料大类
	 */
	@RequestMapping(value = "/scm/product/getPrKind.action")
	@ResponseBody
	public Map<String, Object> getPrKind(String tablename, String fields, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",productKindService.getPrKind(tablename, fields,condition));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitProductKind.action")  
	@ResponseBody 
	public Map<String, Object> submitProductKind(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productKindService.submitProductKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitProductKind.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitProductKind(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productKindService.resSubmitProductKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditProductKind.action")  
	@ResponseBody 
	public Map<String, Object> auditProductKind(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productKindService.auditProductKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditProductKind.action")  
	@ResponseBody 
	public Map<String, Object> resAuditProductKind(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productKindService.resAuditProductKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
