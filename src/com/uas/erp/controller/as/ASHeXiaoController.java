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
import com.uas.erp.service.as.HeXiaoService;
import com.uas.erp.service.ma.ConfigService;

@Controller
public class ASHeXiaoController {

	@Autowired
	private HeXiaoService heXiaoService;

	@Autowired
	private ConfigService configService;

	@RequestMapping("/as/port/getHexiao.action")
	@ResponseBody
	public Map<String, Object> getHexiao(String filters) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String, Object> filterMap = FlexJsonUtil.fromJson(filters);
		modelMap.put("data", heXiaoService.getHexiao(filterMap));
		return modelMap;
	}
	
	@RequestMapping("/as/port/getHexiaoDetail.action")
	@ResponseBody
	public Map<String, Object> getHexiaoDetail(HttpSession session, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", heXiaoService.getHexiaoDetail(code));
		return modelMap;
	}
	
	/**
	 * 核销单批量转出库单
	 */
	@RequestMapping(value = "/as/port/hexiaoToProdIO.action")
	@ResponseBody
	public Map<String, Object> hexiaoToProdIO(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", heXiaoService.hexiaoToProdIO(data));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 核销单批量删除
	 */
	@RequestMapping(value = "/as/port/hexiaoDelete.action")
	@ResponseBody
	public Map<String, Object> hexiaoDelete(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", heXiaoService.hexiaoDelete(data));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/as/port/getHexiaoList.action")
	@ResponseBody
	public Page<Map<String, Object>> getHexiaoList(int page, int start, int limit, String filters) {
		Map<String, Object> filterMap = FlexJsonUtil.fromJson(filters);
		return heXiaoService.getHexiaoList(page, start, limit, filterMap);
	}
}
