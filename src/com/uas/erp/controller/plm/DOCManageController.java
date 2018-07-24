package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.DOCManageService;

@Controller
public class DOCManageController {
	@Autowired
	private DOCManageService dOCManageService;
	@RequestMapping("plm/document/getAllDirectorys.action")
	@ResponseBody
	public Map<String,Object> getAllDirectorys(HttpSession session){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		modelMap.put("tree", dOCManageService.getAllDirectorys());
		modelMap.put("sucess", true);
		return modelMap;
	}
}
