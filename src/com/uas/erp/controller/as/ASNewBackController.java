package com.uas.erp.controller.as;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.model.Page;
import com.uas.erp.service.as.NewBackService;
import com.uas.erp.service.ma.ConfigService;

@Controller
public class ASNewBackController {

	@Autowired
	private NewBackService newBackService;

	@Autowired
	private ConfigService configService;

	@RequestMapping("/as/port/getNewBack.action")
	@ResponseBody
	public Map<String, Object> getNewBack(String filters) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String, Object> filterMap = FlexJsonUtil.fromJson(filters);
		modelMap.put("data", newBackService.getNewBack(filterMap));
		return modelMap;
	}
	
	@RequestMapping("/as/port/getNewBackDetail.action")
	@ResponseBody
	public Map<String, Object> getNewBackDetail(HttpSession session, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", newBackService.getNewBackDetail(code));
		return modelMap;
	}
	
	/**
	 * 新品退货单批量转出库单
	 */
	@RequestMapping(value = "/as/port/newBackToProdIO.action")
	@ResponseBody
	public Map<String, Object> newBackToProdIO(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", newBackService.newBackToProdIO(data));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 新品退货单批量删除
	 */
	@RequestMapping(value = "/as/port/newBackDelete.action")
	@ResponseBody
	public Map<String, Object> newBackDelete(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", newBackService.newBackDelete(data));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/as/port/getNewBackList.action")
	@ResponseBody
	public Page<Map<String, Object>> getNewBackList(int page, int start, int limit, String filters) {
		Map<String, Object> filterMap = FlexJsonUtil.fromJson(filters);
		return newBackService.getNewBackList(page, start, limit, filterMap);
	}
}
