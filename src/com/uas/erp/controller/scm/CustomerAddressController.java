package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.CustomerAddressService;

@Controller
public class CustomerAddressController extends BaseController {
	@Autowired
	private CustomerAddressService customerAddressService;
	
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateCustomerAddress.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerAddressService.updateCustomerById(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/customer/getcustomerid.action")  
	@ResponseBody 
	public Map<String, Object> getCustomerid(String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int id = customerAddressService.getCustomerid(code);
		modelMap.put("success", true);
		modelMap.put("id", id);
		return modelMap;
	}
}
