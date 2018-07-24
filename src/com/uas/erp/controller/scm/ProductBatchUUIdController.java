package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductBatchUUIdService;

@Controller
public class ProductBatchUUIdController {
	@Autowired
	private ProductBatchUUIdService  productBatchUUIdService;
	/**
	 * 获取标准器件库元件种类
	 */
	@RequestMapping("/scm/product/getProductB2CKindTree.action")  
	@ResponseBody 
	public Map<String, Object> getProductKindTree(String type, Long parentid ) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("tree",productBatchUUIdService.getProductKindTree(type, parentid));
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 根据类目ID查找器件信息
	 */
	@RequestMapping("/scm/product/getProductComponent.action")  
	@ResponseBody 
	public Map<String, Object> getProductComponent(Long kindId,int page,int pageSize,String orispeccode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("gridStore",productBatchUUIdService.getProductComponent(kindId,page,pageSize,orispeccode));
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 根据原厂型号获取uuid,调用商城接口
	 */
	@RequestMapping("/scm/product/getUUIdByCode.action")  
	@ResponseBody 
	public Map<String, Object> getUUIdByCode(String code ) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("gridStore",productBatchUUIdService.getUUIdByCode(code));
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 根据UUid获取相关数据，调用商城接口
	 */
	@RequestMapping("/scm/product/getByUUIds.action")  
	@ResponseBody 
	public Map<String, Object> getByUUIds(String ids ) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("gridStore",productBatchUUIdService.getByUUIds(ids));
		modelMap.put("success", true);
		return modelMap;
	}	
	
	/**
	 * 获取访问标准器件库页面的access_token，一次使用
	 */
	@RequestMapping("/scm/product/getPageAccess.action")  
	@ResponseBody 
	public Map<String, Object> getPageAccess() {
		return productBatchUUIdService.getPageAccess();
	}
	
	/**
	 * 装载所有 有原厂型号的物料
	 */
	@RequestMapping("/scm/product/loadAllProd.action")  
	@ResponseBody 
	public Map<String, Object> loadAllProd(String caller,String condition,String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBatchUUIdService.loadAllProd(caller,condition,code);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 装载选定 有原厂型号的物料
	 */
	@RequestMapping("/scm/product/loadProd.action")  
	@ResponseBody 
	public Map<String, Object> loadProd(String caller,
			String data, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBatchUUIdService.loadProd(caller,data,code);
		modelMap.put("success", true);
		return modelMap;
	}	
	
	/**
	 * 解除匹配平台料号关系
	 */
	@RequestMapping("/scm/product/removeUUId.action")  
	@ResponseBody 
	public Map<String, Object> removeUUId(String caller,String code,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBatchUUIdService.removeUUId(caller,code,data);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 根据原厂型号查找类目树形结构
	 * @param caller
	 * @param code 原厂型号
	 * @return
	 */
	@RequestMapping("/scm/product/searchByOrispecode.action")  
	@ResponseBody 
	public Map<String, Object> searchByOrispecode(String caller,String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("tree", productBatchUUIdService.searchByOrispecode(caller,code));
		modelMap.put("success", true);		
		return modelMap;
	}	
	/**
	 * 根据类目模糊查找类目树形结构
	 * @param caller
	 * @param code 类目编号
	 * @return
	 */
	@RequestMapping("/scm/product/searchByKindcode.action")  
	@ResponseBody 
	public Map<String, Object> searchByKindcode(String caller,String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("tree", productBatchUUIdService.searchByKindcode(caller,code));
		modelMap.put("success", true);		
		return modelMap;
	}	
	
	/**
	 * 确认选择
	 */
	@RequestMapping("/scm/product/confirmUUId.action")  
	@ResponseBody 
	public Map<String, Object> confirmUUId(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBatchUUIdService.confirmUUId(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	
}
