package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.JSONTree;
import com.uas.erp.service.scm.VendorKindService;


@Controller
public class VendorKindController {
	@Autowired
	private VendorKindService vendorKindService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/saveVendorKind.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorKindService.saveVendorKind(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除采购单数据
	 * 包括采购明细
	 */
	@RequestMapping("/scm/purchase/deleteVendorKind.action")  
	@ResponseBody 
	public Map<String, Object> deletePayments(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorKindService.deleteVendorKind(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updateVendorKind.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorKindService.updateVendorKindById(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/scm/purchase/bannedVendorKind.action")  
	@ResponseBody 
	public Map<String, Object> end(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorKindService.banned(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/scm/purchase/resBannedVendorKind.action")  
	@ResponseBody 
	public Map<String, Object> resEnd(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorKindService.resBanned(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 根据父节点加载子节点
	 */
	@RequestMapping(value="/scm/purchase/getVendorKindTree.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(int parentid) throws Exception{
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = vendorKindService.getJsonTrees(parentid);
		modelMap.put("tree", tree);
		return modelMap;
	}
	
	@RequestMapping(value="/scm/purchase/getVendorCode.action")
	@ResponseBody
	public Map<String, Object> getProductKindNum(String kind) throws Exception{
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("code", vendorKindService.getVendorCodeByKind(kind));
		modelMap.put("success", true);
		return modelMap;
	}
}
