package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.common.SingleGridPanelService;
import com.uas.erp.service.scm.PreSaleForecastService;

@Controller
public class PreSaleForecastController extends BaseController {
	@Autowired
	private PreSaleForecastService preSaleForecastService;
	@Autowired
	private SingleGridPanelService singleGridPanelService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/savePreSaleForecast.action")  
	@ResponseBody 
	public Map<String, Object> savePreSaleForecast(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleForecastService.savePreSaleForecast(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deletePreSaleForecast.action")  
	@ResponseBody 
	public Map<String, Object> deletePreSaleForecast(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleForecastService.deletePreSaleForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除明晰行
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deletePreSaleForecastDetail.action")  
	@ResponseBody 
	public Map<String, Object> deletePreSaleForecastDetail(String id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleForecastService.deletePreSaleForecastDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitPreSaleForecast.action")  
	@ResponseBody 
	public Map<String, Object> submitPreSaleForecast(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleForecastService.submitPreSaleForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitPreSaleForecast.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitPreSaleForecast(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleForecastService.resSubmitPreSaleForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditPreSaleForecast.action")  
	@ResponseBody 
	public Map<String, Object> auditPreSaleForecast(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleForecastService.auditPreSaleForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditPreSaleForecast.action")  
	@ResponseBody 
	public Map<String, Object> resAuditPreSaleForecast(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleForecastService.resAuditPreSaleForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updatePreSaleForecast.action")  
	@ResponseBody 
	public Map<String, Object> updatePre(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleForecastService.updatePreSaleForecastById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/sale/PreSaleForecastChangedate.action")
	@ResponseBody
	public Map<String, Object> PreSaleForecastChange(String caller,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleForecastService.savePreSaleForecastChangedate(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	
	@RequestMapping("/scm/sale/getPreGridConfig.action")
	@ResponseBody
	public Map<String, Object> getPreGridFields(String caller, String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GridPanel gridPanel = singleGridPanelService.getGridPanelByCaller(caller, condition, null, null, 1,false,"");
		modelMap.put("fields", gridPanel.getGridFields());
		//这里的columns里面添加了属性dbfind，方便进行dbfind操作。详见com.uas.erp.model.GridColumns的构造函数
		modelMap.put("columns", gridPanel.getGridColumns());
		//B2B里面每次dbfind都要重新查找dbfindsetgrid配置，实在麻烦，
		//所以在grid加载时，直接将dbfindsetgrid配置得到并传到前台
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("limits", gridPanel.getLimits());
		modelMap.put("date",preSaleForecastService.getPreConfig(condition));
		if(condition.equals("")){//表示是单表录入界面
			//为grid空白行设置一些默认值
		} else {//表示是单表显示界面
			modelMap.put("data", gridPanel.getDataString());
		}
		return modelMap;
	}
	
	
	/**
	 * 更新人员预测 
	 * */
	@RequestMapping("/scm/sale/updatePreForecast.action")
	@ResponseBody
	public Map<String,Object> updatePreForecast(String formStore, String param, String caller){
	    Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleForecastService.updatePreForecast(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 复制人员预测
	 * */
	@RequestMapping("/scm/sale/copyPreForecast.action")
	@ResponseBody
	public Map<String,Object> copyPreForecast(int id, String caller,String forecast,String weeks,String weeke,String months,String monthe,String days,String daye){
	    Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", preSaleForecastService.copyPreSaleForecast(id, caller,forecast,weeks,weeke,months,monthe,days,daye));
		modelMap.put("success", true);
		return modelMap;
	}
	
}
