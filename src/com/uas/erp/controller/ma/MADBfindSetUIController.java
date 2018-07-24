package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.ma.MADBfindSetUIService;

@Controller
public class MADBfindSetUIController {
	
	@Autowired
	private MADBfindSetUIService madBfindSetUIService;
	/**
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/ma/saveDBfindSetUI.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		madBfindSetUIService.save(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 */
	@RequestMapping("/ma/deleteDBfindSetUI.action")  
	@ResponseBody 
	public Map<String, Object> delete(HttpSession session, int pu_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除明细行某一条数据
	 */
	@RequestMapping("/ma/deleteDBfindSetUIDetail.action")  
	@ResponseBody 
	public Map<String, Object> deleteDetail(HttpSession session, int pd_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/ma/updateDBfindSetUI.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}
}
