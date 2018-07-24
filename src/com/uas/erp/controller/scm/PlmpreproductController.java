package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.PlmpreproductService;
@Controller
public class PlmpreproductController {
	@Autowired
	private PlmpreproductService plmpreproductService;
	/**
	 * 保存
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/scm/product/savePlmpreproduct.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		plmpreproductService.savePlmpreproduct(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改
	 */
	@RequestMapping("/scm/product/updatePlmpreproduct.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		plmpreproductService.updatePlmpreproductById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deletePlmpreproduct.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		plmpreproductService.deletePlmpreproduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitPlmpreproduct.action")  
	@ResponseBody 
	public Map<String, Object> submitPlmpreproduct(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		plmpreproductService.submitPlmpreproduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitPlmpreproduct.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitPlmpreproduct(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		plmpreproductService.resSubmitPlmpreproduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/product/auditPlmpreproduct.action")  
	@ResponseBody 
	public Map<String, Object> auditPlmpreproduct(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		plmpreproductService.auditPlmpreproduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditPlmpreproduct.action")  
	@ResponseBody 
	public Map<String, Object> resAuditPlmpreproduct(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		plmpreproductService.resAuditPlmpreproduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
