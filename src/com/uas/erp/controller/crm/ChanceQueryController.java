package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.GridPanel;
import com.uas.erp.service.crm.ChanceQueryService;

@Controller
public class ChanceQueryController {
	@Autowired
	private ChanceQueryService chanceQueryService;

	@RequestMapping("/crm/chance/getChanceQuery.action")
	@ResponseBody
	public Map<String, Object> chanceQuery(String condition) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		GridPanel gridPanel = chanceQueryService.getQuery("Chance!CRM!Query",
				condition);
		modelMap.put("fields", gridPanel.getGridFields());
		// 这里的columns里面添加了属性dbfind，方便进行dbfind操作。详见com.uas.erp.model.GridColumns的构造函数
		modelMap.put("columns", gridPanel.getGridColumns());
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
}
