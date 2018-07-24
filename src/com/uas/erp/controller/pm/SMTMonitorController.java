package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.SMTMonitorService;

@Controller
public class SMTMonitorController {
    @Autowired
    private SMTMonitorService SMTMonitorService;
	
    @RequestMapping("/pm/mes/getSMTMonitorStore.action")
	@ResponseBody
	public Map<String, Object> getSMTMonitorStore(String de_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("message", SMTMonitorService.getSMTMonitorStore(de_code));
		modelMap.put("success", true);
		return modelMap;
	}
}
