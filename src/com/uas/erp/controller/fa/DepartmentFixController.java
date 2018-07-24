package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.DepartmentService;

@Controller
public class DepartmentFixController extends BaseController {
	@Autowired
	private DepartmentService departmentService;

	@RequestMapping("/fa/fix/updateDepartmentFix.action")
	@ResponseBody
	public Map<String, Object> updateDocumentSetup(HttpSession session,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		departmentService.updateDepartmentById(null, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
