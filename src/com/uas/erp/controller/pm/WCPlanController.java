package com.uas.erp.controller.pm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.common.SingleGridPanelService;
import com.uas.erp.service.pm.WCPlanService;

@Controller
public class WCPlanController {
	@Autowired
	private WCPlanService wCPlanService;
	@Autowired
	private FilePathService filePathService;
	@Autowired
	private SingleGridPanelService singleGridPanelService;

	@RequestMapping("/pm/make/saveWCPlan.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.saveWCPlan(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteWCPlan.action")
	@ResponseBody
	public Map<String, Object> deleteWCPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.deleteWCPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/make/updateWCPlan.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.updateWCPlan(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitWCPlan.action")
	@ResponseBody
	public Map<String, Object> submitWCPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.submitWCPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitWCPlan.action")
	@ResponseBody
	public Map<String, Object> resSubmitWCPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.resSubmitWCPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditWCPlan.action")
	@ResponseBody
	public Map<String, Object> auditWCPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.auditWCPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditWCPlan.action")
	@ResponseBody
	public Map<String, Object> resAuditWCPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.resAuditWCPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * Excel导入
	 * */
	@RequestMapping("pm/make/ImportExcel.action")
	@ResponseBody
	public String ImportExcel(String caller, int id, int fileId) {
		InputStream is = null;
		String filePath = filePathService.getFilepath(fileId);
		Workbook wbs = null;
		try {
			String ft = filePath.substring(filePath.lastIndexOf(".") + 1);
			is = new FileInputStream(new File(filePath));
			if (ft.equals("xls")) {
				wbs = new HSSFWorkbook(is);
			} else if (ft.equals("xlsx")) {
				wbs = new XSSFWorkbook(is);

			} else {
				return new String(
						"{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为,然后导入'}"
								.getBytes("utf-8"),
						"iso8859-1");
			}
			boolean bool = wCPlanService.ImportExcel(id, wbs,
					filePath.substring(filePath.lastIndexOf("/") + 1),
					caller);
			if (bool) {
				// Excel 解析成功之后要删除
				File file = new File(filePath);
				// 路径为文件且不为空则进行删除
				if (file.isFile() && file.exists()) {
					file.delete();
				}
			}
			String r = "{success: true}";
			return r;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				return new String(
						"{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为,然后导入'}"
								.getBytes("utf-8"),
						"iso8859-1");
			} catch (UnsupportedEncodingException e1) {
				return "{success: false}";
			}
		} finally {
			try {
				is.close();
			} catch (IOException e) {

			}
		}
	}

	/**
	 * 删除明细
	 * */
	@RequestMapping("/pm/make/deleteAllDetails.action")
	@ResponseBody
	public Map<String, Object> deleteAllDetails(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.deleteAllDetails(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 装载工单
	 * */
	@RequestMapping("/pm/make/loadMake.action")
	@ResponseBody
	public Map<String, Object> loadMake(String caller, 
			String data, int wc_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.loadMake(caller, data ,wc_id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 装载全部工单
	 * */
	@RequestMapping("pm/make/loadAllMakeByCondition.action")
	@ResponseBody
	public Map<String, Object> loadAllMakeByCondition(String caller,
			String condition, int wc_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.loadAllMakeByCondition(caller, wc_id, condition);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 装载销售订单
	 * */
	@RequestMapping("/pm/make/loadSale.action")
	@ResponseBody
	public Map<String, Object> loadSale(String caller, 
			String data, int wc_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.loadSale(caller, data ,wc_id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 装载全部销售订单
	 * */
	@RequestMapping("pm/make/loadAllSaleByCondition.action")
	@ResponseBody
	public Map<String, Object> loadAllSaleByCondition(String caller,
			String condition, int wc_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.loadAllSaleByCondition(caller, wc_id, condition);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 缺料运算
	 * */
	@RequestMapping("/pm/make/RunLackMaterial.action")
	@ResponseBody
	public Map<String, Object> RunLackMaterial(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String str =wCPlanService.RunLackMaterial(code, caller);
		modelMap.put("message", str);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * WIP仓缺料运算
	 * */
	@RequestMapping("/pm/make/RunLackWip.action")
	@ResponseBody
	public Map<String, Object> RunLackWip(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.RunLackWip(code, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/pm/make/singleGridPanel.action")
	@ResponseBody
	public Map<String, Object> getGridFields(String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GridPanel gridPanel = singleGridPanelService.getGridPanelByCaller(
				caller, condition, null, null, 1,false,"");
		modelMap.put("fields", gridPanel.getGridFields());
		// 这里的columns里面添加了属性dbfind，方便进行dbfind操作。详见com.uas.erp.model.GridColumns的构造函数
		modelMap.put("columns", gridPanel.getGridColumns());
		// B2B里面每次dbfind都要重新查找dbfindsetgrid配置，实在麻烦，
		// 所以在grid加载时，直接将dbfindsetgrid配置得到并传到前台
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("limits", gridPanel.getLimits());
		modelMap.put("date", wCPlanService.getDateRange(condition));
		if (condition.equals("")) {// 表示是单表录入界面
			// 为grid空白行设置一些默认值
		} else {// 表示是单表显示界面
			modelMap.put("data", gridPanel.getDataString());
		}
		return modelMap;
	}

	@RequestMapping("pm/wcplan/throwpurchasenotify.action")
	@ResponseBody
	public Map<String, Object> NeedThrow(String caller, String data,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.throwPurchaseNotify(caller, data, condition);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/wcplan/throwwipneed.action")
	@ResponseBody
	public Map<String, Object> ThrowWipNeed(String caller, String data,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = wCPlanService.ThrowWipNeed(caller, data, condition);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 装载销售订单
	 * */
	@RequestMapping("/pm/make/loadSaleForecast.action")
	@ResponseBody
	public Map<String, Object> loadSaleForecast(String caller, 
			String data, int wc_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.loadSaleForecast(caller, data ,wc_id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 装载全部销售订单
	 * */
	@RequestMapping("pm/make/loadAllSaleForecastByCondition.action")
	@ResponseBody
	public Map<String, Object> loadAllSaleForecastByCondition(String caller,
			String condition, int wc_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wCPlanService.loadAllSaleForecastByCondition(caller, wc_id, condition);
		modelMap.put("success", true);
		return modelMap;
	}

}
