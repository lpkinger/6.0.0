package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.ma.DBfindSetUIService;


@Controller
public class DBfindSetUIController {

	@Autowired
	private DBfindSetUIService dBfindSetUIService;

	@RequestMapping(value = "/ma/dbfindsetui/getData.action")
	@ResponseBody
	public Map<String, Object> getConfigsByCaller(String id) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		JSONObject obj =dBfindSetUIService.getDbFindSetUIByField(id);
		modelMap.put("formdata", obj.get("formdata"));
		modelMap.put("griddata", obj.get("griddata"));
		modelMap.put("fields", obj.get("fields"));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping(value = "/ma/dbfindsetui/saveDbfindSetUI.action")
	@ResponseBody
	public Map<String, Object> saveDbFindSetUI(HttpSession session, String formStore, String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		
		modelMap.put("id", dBfindSetUIService.saveDbFindSetUI(formStore, gridStore));
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	@RequestMapping(value = "/ma/dbfindsetui/deleteData.action")
	@ResponseBody
	public Map<String, Object> deleteDbFindSetUI(HttpSession session,String id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		
		int result=dBfindSetUIService.deleteDbFindSetUI(id);
		if(result==0){
			modelMap.put("success", true);
		}else {
			modelMap.put("success", false);
		}
		
		return modelMap;
	}

}
