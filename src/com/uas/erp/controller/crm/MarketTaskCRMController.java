package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.MarketTaskCRMService;

@Controller
public class MarketTaskCRMController {
	@Autowired
	private MarketTaskCRMService marketTaskCRMService;
	@Autowired
	private BaseDao baseDao;

	@RequestMapping("crm/toMarketTask.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketTaskCRMService.toMarketTask(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("crm/getReportCode.action")
	@ResponseBody
	public Map<String, Object> get(String mt_code) {
		Object mt_reportcode = baseDao.getFieldDataByCondition("MarketTaskCRM",
				"mt_reportcode", "mt_code='" + mt_code + "'");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("mt_reportcode", mt_reportcode);
		modelMap.put("success", true);
		return modelMap;
	}
}
