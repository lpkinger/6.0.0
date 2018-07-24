package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.ProductDescriptionService;

@Controller
public class ProductDescriptionController extends BaseController {
	@Autowired
	private ProductDescriptionService productDescriptionService;

	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/product/updateProductDescription.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productDescriptionService.updateProductById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
}
