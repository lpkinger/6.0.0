package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.AssService;

@Controller
public class AssController extends BaseController {
	@Autowired
	private AssService assService;

	@RequestMapping("/fa/ars/saveAss.action")
	@ResponseBody
	public Map<String, Object> saveAss(HttpSession session, String caller,
			String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assService.saveAss(caller, param);
		modelMap.put("success", true);
		return modelMap;
	}
}
