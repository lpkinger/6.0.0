package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Editor;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.common.SingleGridPanelService;
import com.uas.erp.service.hr.KpiPositionService;

@Controller
public class KpiPositionController {
	@Autowired
	private KpiPositionService kpiPositionService;
	@Autowired
	private SingleGridPanelService singleGridPanelService;

	@RequestMapping(value = "/hr/kpi/saveKpiPosition.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiPositionService
				.saveKpiPosition(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/hr/kpi/singleGridPanel.action")
	@ResponseBody
	public Map<String, Object> getGridFields(String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GridPanel gridPanel = singleGridPanelService.getGridPanelByCaller(
				caller, "1=1", null, null, 1,false,"");
		List<GridFields> fields = gridPanel.getGridFields();
		GridFields g = new GridFields();
		g.setType("bool");
		g.setName("isUsed");
		fields.add(g);
		modelMap.put("fields", fields);
		// 这里的columns里面添加了属性dbfind，方便进行dbfind操作。详见com.uas.erp.model.GridColumns的构造函数
		List<GridColumns> columns = gridPanel.getGridColumns();
		GridColumns c = new GridColumns();
		c.setDataIndex("isUsed");
		c.setHeader("是否启动");
		c.setText("是否启动");
		c.setWidth(100);
		c.setXtype("checkcolumn");
		c.setReadOnly(false);
		c.setEditor(new Editor("checkbox"));
		columns.add(c);
		modelMap.put("columns", columns);
		// B2B里面每次dbfind都要重新查找dbfindsetgrid配置，实在麻烦，
		// 所以在grid加载时，直接将dbfindsetgrid配置得到并传到前台
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("limits", gridPanel.getLimits());
		modelMap.put("data", kpiPositionService.show(gridPanel.getDataString(),
				caller, condition));
		return modelMap;
	}
}
