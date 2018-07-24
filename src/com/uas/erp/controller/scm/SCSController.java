package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.SCSService;
@Controller
public class SCSController {
	@Autowired
	private SCSService sCSService;
	
	@RequestMapping("scm/qc/saveSCS.action")
	@ResponseBody
	public Map<String,Object> updateAccountGCS(String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sCSService.saveSCSById(null, param);
		modelMap.put("success", true);
		return modelMap;
	}

}
