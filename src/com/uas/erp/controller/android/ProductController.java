package com.uas.erp.controller.android;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.SingleFormItemsService;

@Controller("androidProductController")
public class ProductController {
	
	@Autowired
	private SingleFormItemsService singleFormItemsService;
	
	/**
	 * 查询物料资料
	 */
	@RequestMapping("/android/scm/product/query.action")
	@ResponseBody
	public Map<String, Object> checkLogin(HttpServletRequest request, HttpSession session, ModelMap modelMap, String code) {
		if (code != null) {
			Map<String, Object> map = new HashMap<String, Object>();
			Object name = singleFormItemsService.getFieldData("Product", "pr_detail", "pr_code='" + code + "'");
			map.put("data", name);
			return map;
		}
		return null;
	}
}
