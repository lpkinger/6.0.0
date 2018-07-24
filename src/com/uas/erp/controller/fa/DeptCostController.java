package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.DeptCostService;

@Controller
public class DeptCostController {

	@Autowired
	private DeptCostService deptCostService;

	/**
	 * 部门费用
	 */
	@RequestMapping("/fa/ars/getDeptCost.action")
	@ResponseBody
	public Map<String, Object> getDeptCost(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", deptCostService.getDeptCost(condition));
		modelMap.put("columns", deptCostService.getGridColumnsByDepts(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 人员费用
	 */
	@RequestMapping("/fa/ars/getEmplCost.action")
	@ResponseBody
	public Map<String, Object> getEmplCost(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", deptCostService.getEmplCost(condition));
		modelMap.put("column", deptCostService.getEmplColumn(condition));
		modelMap.put("success", true);
		return modelMap;
	}

}
