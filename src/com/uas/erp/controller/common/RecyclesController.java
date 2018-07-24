package com.uas.erp.controller.common;

import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.common.RecyclesService;

@Controller
public class RecyclesController {
	
	@Autowired
	private RecyclesService recyclesService;
	
	/**
	 * 调取回收站数据
	 */
	@RequestMapping("/common/recycle/getRecycles.action")  
	@ResponseBody 
	public Map<String, Object> deleteCommon(HttpSession session, String caller, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = recyclesService.getRecycles(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
}
