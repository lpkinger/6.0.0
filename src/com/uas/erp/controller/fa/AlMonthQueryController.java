package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.fa.AlMonthQueryService;
import com.uas.erp.service.fa.AutoDepreciationService;

@Controller("alQueryController")
public class AlMonthQueryController extends BaseController {
	@Autowired
	private AlMonthQueryService alMonthQueryService;
	@Autowired
	private AutoDepreciationService autoDepreciationService;

	/**
	 * 刷新银行存款总账
	 */
	@RequestMapping("/fa/gs/ArQueryController/refreshArQuery.action")
	@ResponseBody
	public Map<String, Object> refreshArQuery(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int yearmonth = autoDepreciationService.getCurrentYearmonthGS();
		alMonthQueryService.refreshArQuery(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 银行存款总账查询
	 * 
	 * @param _businessLimit
	 * 
	 */
	@RequestMapping("/fa/gs/ArQueryController/getArQuery.action")
	@ResponseBody
	public Map<String, Object> getCmQuery(HttpSession session, String condition, Boolean _businessLimit) {
		Map<String, Object> modelMap = new HashMap<String, Object>();

		GridPanel gridPanel = alMonthQueryService.getArQuery(condition);
		modelMap.put("fields", gridPanel.getGridFields());
		modelMap.put("columns", gridPanel.getGridColumns());
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("limits", gridPanel.getLimits());
		if (!condition.equals("")) {
			modelMap.put("data", gridPanel.getData());
		}
		return modelMap;
	}

	/**
	 * 刷新银行存款明细账
	 */
	@RequestMapping("/fa/gs/ArQueryController/getArDetailById.action")
	@ResponseBody
	public Map<String, Object> getCmDetailById(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", alMonthQueryService.getArDetailById(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 银行现金日记账
	 */
	@RequestMapping("/fa/gs/ArQueryController/getArDayDetail.action")
	@ResponseBody
	public Map<String, Object> getArDayDetail(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", alMonthQueryService.getArDayDetail(condition));
		modelMap.put("success", true);
		return modelMap;
	}
}
