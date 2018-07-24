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
import com.uas.erp.service.fa.AutoDepreciationService;
import com.uas.erp.service.fa.CmQueryService;

@Controller("cmQueryController")
public class CmQueryController extends BaseController {
	@Autowired
	private CmQueryService cmQueryService;
	@Autowired
	private AutoDepreciationService autoDepreciationService;

	/**
	 * 搴旀敹鎬昏处鏌ヨ
	 * 
	 * @param _businessLimit
	 *            涓氬姟鎺у埗
	 */
	@RequestMapping("/fa/ars/CmQueryController/getCmQuery.action")
	@ResponseBody
	public Map<String, Object> getCmQuery(HttpSession session,
			String condition, Boolean _businessLimit) {
		Map<String, Object> modelMap = new HashMap<String, Object>();

		GridPanel gridPanel = cmQueryService.getCmQuery(condition);
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
	 * 涓氬姟鍛樺簲鏀舵�璐︽煡璇�
	 * 
	 * @param _businessLimit
	 *            涓氬姟鎺у埗
	 */
	@RequestMapping("/fa/ars/CmQueryController/getSmQuery.action")
	@ResponseBody
	public Map<String, Object> getSmQuery(HttpSession session,
			String condition, Boolean _businessLimit) {
		Map<String, Object> modelMap = new HashMap<String, Object>();

		modelMap.put("data", cmQueryService.getSmQuery(condition));
		return modelMap;
	}

	/**
	 * 鏌ヨ
	 */
	@RequestMapping("/fa/ars/CmQueryController/getCmDetailQuery.action")
	@ResponseBody
	public Map<String, Object> getCmDetailQuery(HttpSession session,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", cmQueryService.getCmDetailQuery(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 鏌ヨ
	 */
	@RequestMapping("/fa/ars/CmQueryController/getCmDetailById.action")
	@ResponseBody
	public Map<String, Object> getCmDetailById(HttpSession session,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", cmQueryService.getCmDetailById(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 鏌ヨ
	 */
	@RequestMapping("/fa/ars/CmQueryController/getCmDetailByIdDetail.action")
	@ResponseBody
	public Map<String, Object> getCmDetailByIdDetail(HttpSession session,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", cmQueryService.getCmDetailByIdDetail(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 鍒锋柊
	 */
	@RequestMapping("/fa/ars/CmQueryController/refreshCmQuery.action")
	@ResponseBody
	public Map<String, Object> refreshCmQuery(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int yearmonth = autoDepreciationService.getCurrentYearmonthAR();
		cmQueryService.refreshCmQuery(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 搴旀敹鎬昏处鏌ヨ
	 * 
	 * @param _businessLimit
	 *            涓氬姟鎺у埗
	 */
	@RequestMapping("/fa/ars/CmQueryController/getCmCopQuery.action")
	@ResponseBody
	public Map<String, Object> getCmCopQuery(HttpSession session,
			String condition, Boolean _businessLimit) {
		Map<String, Object> modelMap = new HashMap<String, Object>();

		GridPanel gridPanel = cmQueryService.getCmCopQuery(condition);
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
	 * 鏌ヨ
	 */
	@RequestMapping("/fa/ars/CmQueryController/getCmCopDetailQuery.action")
	@ResponseBody
	public Map<String, Object> getCmCopDetailQuery(HttpSession session,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", cmQueryService.getCmCopDetailQuery(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 鏌ヨ
	 */
	@RequestMapping("/fa/ars/CmQueryController/getCmCopDetailById.action")
	@ResponseBody
	public Map<String, Object> getCmCopDetailById(HttpSession session,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", cmQueryService.getCmCopDetailById(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 鏌ヨ
	 */
	@RequestMapping("/fa/ars/CmQueryController/getCmCopDetailByIdDetail.action")
	@ResponseBody
	public Map<String, Object> getCmCopDetailByIdDetail(HttpSession session,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", cmQueryService.getCmCopDetailByIdDetail(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 鍒锋柊
	 */
	@RequestMapping("/fa/ars/CmQueryController/refreshCmCopQuery.action")
	@ResponseBody
	public Map<String, Object> refreshCmCopQuery(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int yearmonth = autoDepreciationService.getCurrentYearmonthAR();
		cmQueryService.refreshCmCopQuery(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 刷新
	 */
	@RequestMapping("/fa/ars/CmQueryController/refreshQuery.action")
	@ResponseBody
	public Map<String, Object> refreshQuery(HttpSession session,String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		cmQueryService.refreshQuery(condition);
		modelMap.put("success", true);
		return modelMap;
	}
}
