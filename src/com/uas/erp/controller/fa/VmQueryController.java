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
import com.uas.erp.service.fa.VmQueryService;

@Controller("vmQueryController")
public class VmQueryController extends BaseController {
	@Autowired
	private VmQueryService vmQueryService;
	@Autowired
	private AutoDepreciationService autoDepreciationService;

	/**
	 * 查询
	 */
	@RequestMapping("/fa/arp/VmQueryController/getVmQuery.action")
	@ResponseBody
	public Map<String, Object> getVmQuery(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GridPanel gridPanel = vmQueryService.getVmQuery("", condition);
		modelMap.put("fields", gridPanel.getGridFields());
		// 这里的columns里面添加了属性dbfind，方便进行dbfind操作。详见com.uas.erp.model.GridColumns的构造函数
		modelMap.put("columns", gridPanel.getGridColumns());
		// B2B里面每次dbfind都要重新查找dbfindsetgrid配置，实在麻烦，
		// 所以在grid加载时，直接将dbfindsetgrid配置得到并传到前台
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("limits", gridPanel.getLimits());
		if (condition.equals("")) {// 表示是单表录入界面
			// 为grid空白行设置一些默认值
		} else {// 表示是单表显示界面
			modelMap.put("data", gridPanel.getDataString());
		}
		return modelMap;

	}

	/**
	 * 查询
	 */
	@RequestMapping("/fa/arp/VmQueryController/getVmDetailQuery.action")
	@ResponseBody
	public Map<String, Object> getVmDetailQuery(HttpSession session,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", vmQueryService.getVmDetailQuery(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 查询
	 */
	@RequestMapping("/fa/arp/VmQueryController/getVmDetailById.action")
	@ResponseBody
	public Map<String, Object> getVmDetailById(HttpSession session,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", vmQueryService.getVmDetailById(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 查询
	 */
	@RequestMapping("/fa/arp/VmQueryController/getVmDetailByIdDetail.action")
	@ResponseBody
	public Map<String, Object> getVmDetailByIdDetail(HttpSession session,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", vmQueryService.getVmDetailByIdDetail(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 刷新
	 */
	@RequestMapping("/fa/arp/VmQueryController/refreshVmQuery.action")
	@ResponseBody
	public Map<String, Object> refreshVmQuery(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int yearmonth = autoDepreciationService.getCurrentYearmonthAP();
		vmQueryService.refreshVmQuery(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 查询
	 */
	@RequestMapping("/fa/arp/VmQueryController/getVmCopQuery.action")
	@ResponseBody
	public Map<String, Object> getVmCopQuery(HttpSession session,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();

		GridPanel gridPanel = vmQueryService.getVmCopQuery("", condition);
		modelMap.put("fields", gridPanel.getGridFields());
		// 这里的columns里面添加了属性dbfind，方便进行dbfind操作。详见com.uas.erp.model.GridColumns的构造函数
		modelMap.put("columns", gridPanel.getGridColumns());
		// B2B里面每次dbfind都要重新查找dbfindsetgrid配置，实在麻烦，
		// 所以在grid加载时，直接将dbfindsetgrid配置得到并传到前台
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("limits", gridPanel.getLimits());
		if (condition.equals("")) {// 表示是单表录入界面
			// 为grid空白行设置一些默认值
		} else {// 表示是单表显示界面
			modelMap.put("data", gridPanel.getDataString());
		}
		return modelMap;

	}

	/**
	 * 查询
	 */
	@RequestMapping("/fa/arp/VmQueryController/getVmCopDetailQuery.action")
	@ResponseBody
	public Map<String, Object> getVmCopDetailQuery(HttpSession session,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", vmQueryService.getVmCopDetailQuery(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 查询
	 */
	@RequestMapping("/fa/arp/VmQueryController/getVmCopDetailById.action")
	@ResponseBody
	public Map<String, Object> getVmCopDetailById(HttpSession session,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", vmQueryService.getVmCopDetailById(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 查询
	 */
	@RequestMapping("/fa/arp/VmQueryController/getVmCopDetailByIdDetail.action")
	@ResponseBody
	public Map<String, Object> getVmCopDetailByIdDetail(HttpSession session,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", vmQueryService.getVmCopDetailByIdDetail(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 刷新
	 */
	@RequestMapping("/fa/arp/VmQueryController/refreshVmCopQuery.action")
	@ResponseBody
	public Map<String, Object> refreshVmCopQuery(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int yearmonth = autoDepreciationService.getCurrentYearmonthAP();
		vmQueryService.refreshVmCopQuery(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}
}
