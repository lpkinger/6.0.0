package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.GCSService;


@Controller
public class GCSController {
	@Autowired
	private GCSService gCSService;
	
	@RequestMapping("scm/qc/saveGCS.action")
	@ResponseBody
	public Map<String,Object> updateAccountGCS(String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		gCSService.saveGCSById(null, param);
		modelMap.put("success", true);
		return modelMap;
	}

}
