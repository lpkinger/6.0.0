package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.AccessChangeService;

@Controller
public class AccessChangeController extends BaseController {
	@Autowired
	private AccessChangeService accessChangeService;

	@RequestMapping("/fa/gs/saveAccessChange.action")
	@ResponseBody
	public Map<String, Object> saveAssetsLocation(HttpSession session,
			String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		accessChangeService.saveAccessChange(caller, param);
		modelMap.put("success", true);
		return modelMap;
	}
}
