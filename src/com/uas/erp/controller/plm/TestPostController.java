package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.plm.TestPostService;

/**
 * 过账测试
 */
@Controller
public class TestPostController {
	
	@Autowired
	private TestPostService testPostService;
	
	@RequestMapping("/plm/test/initPurchase.action")
	@ResponseBody 
	public Map<String, Object> initPurchase(HttpSession session, int count) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", testPostService.initPurchase(count));
		return modelMap;
	}
	
	@RequestMapping("/plm/test/initProdIOPurc.action")
	@ResponseBody 
	public Map<String, Object> initProdIOPurc(HttpSession session, int count, String data, String piclass, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", testPostService.initProdIOPurc(count, data,piclass,caller));
		return modelMap;
	}
	
	@RequestMapping("/plm/test/postProdIOPurc.action")
	@ResponseBody 
	public Map<String, Object> postProdIOPurc(HttpSession session, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", testPostService.postProdIOPurc(code));
		return modelMap;
	}
	
	@RequestMapping("/plm/test/clearProdIOPurc.action")
	@ResponseBody 
	public Map<String, Object> clearProdIOPurc(HttpSession session, String code, String codes) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		testPostService.clearProdIOPurc(code, codes);
		modelMap.put("success", true);
		return modelMap;
	}
}
