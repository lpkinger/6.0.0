package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.ma.MADBfindSetDetailService;

@Controller
public class MADBfindSetDetailController {
	
	@Autowired
	private MADBfindSetDetailService madBfindSetDetailService;
	/**
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/ma/saveDSD.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		madBfindSetDetailService.save(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 */
	@RequestMapping("/ma/deleteDSD.action")  
	@ResponseBody 
	public Map<String, Object> delete(HttpSession session, int pu_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除明细行某一条数据
	 */
	@RequestMapping("/ma/deleteDSDDetail.action")  
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
	@RequestMapping("/ma/updateDSD.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}
}
