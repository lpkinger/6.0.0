package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.CountryService;

@Controller
public class CountryControlller extends BaseController {
	@Autowired
	private CountryService countryService;
	/**
	 * 保存Payments
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/saveCountry.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		countryService.saveCountry(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/purchase/deleteCountry.action")  
	@ResponseBody 
	public Map<String, Object> deletePayments(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		countryService.deleteCountry(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/scm/purchase/updateCountry.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		countryService.updateCountryById(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
}
