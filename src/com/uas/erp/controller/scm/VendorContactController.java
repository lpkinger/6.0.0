package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.VendorContactService;

@Controller
public class VendorContactController {
	@Autowired
	private VendorContactService vendorContactService;

	/**
	 * 修改vendor
	 */
	@RequestMapping("/scm/purchase/updateVendorContact.action")
	@ResponseBody
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorContactService.updateVendor(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/purchase/saveVendContact.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorContactService.saveVendContact(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/purchase/updateVendContact.action")  
	@ResponseBody 
	public Map<String, Object> updateVendContact(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorContactService.updateVendContact(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
}
