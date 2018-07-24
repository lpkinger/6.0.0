package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.CSCService;

@Controller
public class CSCController {
	
	@Autowired
	private CSCService cSCService;
	
	@RequestMapping("scm/qc/saveCSC.action")
	@ResponseBody
	public Map<String,Object> updateAccountCSC(String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		cSCService.saveCSCById(null, param);
		modelMap.put("success", true);
		return modelMap;
	}

}
