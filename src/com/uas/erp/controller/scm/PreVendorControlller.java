package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.PreVendorService;


@Controller
public class PreVendorControlller extends BaseController {
	@Autowired
	private PreVendorService prevendorService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/savePreVendor.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prevendorService.savePreVendor(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/purchase/deletePreVendor.action")  
	@ResponseBody 
	public Map<String, Object> deletePreVendor(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prevendorService.deletePreVendor(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 */
	@RequestMapping("/scm/purchase/updatePreVendor.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prevendorService.updatePreVendorById(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitPreVendor.action")  
	@ResponseBody 
	public Map<String, Object> submitPreVendor(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prevendorService.submitPreVendor(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitPreVendor.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitPreVendor(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prevendorService.resSubmitPreVendor(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditPreVendor.action")  
	@ResponseBody 
	public Map<String, Object> auditPreVendor(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prevendorService.auditPreVendor(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditPreVendor.action")  
	@ResponseBody 
	public Map<String, Object> resAuditPreVendor(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prevendorService.resAuditPreVendor(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转供应商
	 */
	@RequestMapping("/scm/purchase/turnVendor.action")  
	@ResponseBody 
	public Map<String, Object> turnVendor(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int veid = prevendorService.turnVendor(id);
		modelMap.put("id", veid);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转供应商基本资料
	 */
	@RequestMapping("/scm/purchase/turnVendorBase.action")  
	@ResponseBody 
	public Map<String, Object> turnVendorBase(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int veid = prevendorService.turnVendorBase(id);
		modelMap.put("id", veid);
		modelMap.put("success", true);
		return modelMap;
	}
}
