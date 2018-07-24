package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.JSONTree;
import com.uas.erp.service.scm.CustomerKindService;

@Controller
public class CustomerKindController {
	@Autowired
	private CustomerKindService customerKindService;
	/**
	 * 保存CustomerKind
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/scm/sale/saveCustomerKind.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerKindService.saveCustomerKind(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/sale/updateCustomerKind.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerKindService.updateCustomerKindById(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteCustomerKind.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerKindService.deleteCustomerKind(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/scm/sale/bannedCustomerKind.action")  
	@ResponseBody 
	public Map<String, Object> end(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerKindService.end(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/scm/sale/resBannedCustomerKind.action")  
	@ResponseBody 
	public Map<String, Object> resEnd(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerKindService.resEnd(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 根据父节点加载子节点
	 */
	@RequestMapping(value="/scm/sale/getCustomerKindTree.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(int parentid) throws Exception{
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = customerKindService.getJsonTrees(parentid);
		modelMap.put("tree", tree);
		return modelMap;
	}
	
	/**
	 * 根据{id}取对应的编号
	 */
	@RequestMapping(value="/scm/sale/getCustomerKindNum.action")
	@ResponseBody
	public Map<String, Object> getProductKindNum(int id) throws Exception{
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("number", customerKindService.getCustomerKindNum(id));
		modelMap.put("success", true);
		return modelMap;
	}
}
