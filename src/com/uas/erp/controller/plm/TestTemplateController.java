package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.plm.TestTemplateService;

@Controller
public class TestTemplateController {
	@Autowired
	private TestTemplateService testTemplateService;
	@RequestMapping("/plm/test/saveTestTemplate.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		testTemplateService.saveTestTemplate(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/test/updateTestTemplate.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		testTemplateService.updateTestTemplate(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	@RequestMapping("/plm/test/deleteTestTemplate.action")  
	@ResponseBody 
	public Map<String, Object> delete(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		testTemplateService.deleteTestTemplate(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
