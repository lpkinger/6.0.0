package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

@Controller
public class InventoryPostrestoreController extends BaseController {

	
	
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/fa/fix/confirmInventoryPostrestore.action")  
	@ResponseBody 
	public Map<String, Object> confirmInventoryPostrestore(HttpSession session, String caller, String param) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		/*此处书写对应的service层的操作*/ 
//		System.out.println("caller="+caller);
		
		modelMap.put("success", true);
		return modelMap;
	}
	
}
