package com.uas.erp.controller.oa;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.SchedulerResourceService;

@Controller
public class SchedulerResourceController {
	@Autowired
	private SchedulerResourceService schedulerResourceService;
	@RequestMapping("/oa/getSchedulerResourceData.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("res", schedulerResourceService.getSchedulerResourceData(caller));
		modelMap.put("success", true);
	    return modelMap;
	}
}
