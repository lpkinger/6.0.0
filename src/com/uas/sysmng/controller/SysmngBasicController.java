package com.uas.sysmng.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.sysmng.service.SysmngBasicService;

@Controller
@RequestMapping("/sysmng")
public class SysmngBasicController {
	
	@Autowired
	private SysmngBasicService sysmngBasicService;
	@Autowired
	private BaseDao baseDao;
	
	@RequestMapping("/default.action")
	@ResponseBody
	public ModelAndView Default(HttpSession session) {
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("employee",(Employee)session.getAttribute("employee"));
		return new ModelAndView("sysmng/default",params);
	}
	@RequestMapping(value = "/getDictionaryData.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getData(HttpServletRequest request,String condition, int page, int pageSize,String tableName) {
		Map<String,Object> map = new HashMap<String,Object>();
		List<Map<String, Object>> modelMap =sysmngBasicService.getDictionaryData(condition
				, page, pageSize,tableName);
		map.put("data", modelMap);
		return map;
	}
	

	@RequestMapping(value = "/singleGrid1Panel.action")
	@ResponseBody
	public Map<String, Object> getGrid1Panel(String caller) {
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		
		List<Map<String,Object>> Grid1Detail =sysmngBasicService.getGrid1PanelByCaller(caller);
		
		modelMap.put("Grid1Detail", Grid1Detail);
		return modelMap;
	}
	@RequestMapping(value = "/singleGrid2Panel.action")
	@ResponseBody
	public Map<String, Object> getGrid2Panel(String caller) {
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		
		List<Map<String,Object>> Grid2Detail =sysmngBasicService.getGrid2PanelByCaller(caller);
		
		modelMap.put("Grid2Detail", Grid2Detail);
		return modelMap;
	}
	@RequestMapping(value = "/saveGrid1detail.action")
	@ResponseBody
	public Map<String,Object> saveGrid1Fields(String addId,String deletedId) {
	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		
		Boolean flag= sysmngBasicService.saveGrid1PanelById(addId,deletedId);
		
		modelMap.put("success",flag);
		return modelMap;
	}
	@RequestMapping(value = "/saveGrid2detail.action")
	@ResponseBody
	public Map<String,Object> saveGrid2Fields(String addId,String deletedId) {
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		
		Boolean flag= sysmngBasicService.saveGrid2PanelById(addId,deletedId);
		
		modelMap.put("success",flag);
		return modelMap;
	}
	
	
	

	@RequestMapping("/checkmodulepower.action")
	@ResponseBody
	public Map<String,Object> checkModulePower(HttpSession session,String emCode,String moduleCode) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		modelMap.put("power",sysmngBasicService.checkModulePower(emCode, moduleCode));
		return modelMap;
	}

}
