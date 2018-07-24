package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.pm.CompareBomService;

@Controller
public class CompareBomController {

	@Autowired
	private CompareBomService compareBomService;

	/**
	 * 制造通知单批量转制造单
	 */
	@RequestMapping("/pm/bom/compareBom.action")
	@ResponseBody
	public Map<String, Object> vastTurnMake(String condition,
			boolean bd_single, boolean bd_difbom, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GridPanel gridPanel = compareBomService.getGridData(condition,
				bd_single, bd_difbom, caller);
		modelMap.put("fields", gridPanel.getGridFields());
		modelMap.put("columns", gridPanel.getGridColumns());
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("limits", gridPanel.getLimits());
		modelMap.put("data", gridPanel.getDataString());
		return modelMap;
	}

}
