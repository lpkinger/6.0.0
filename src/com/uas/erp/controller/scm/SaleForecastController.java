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
import com.uas.erp.service.scm.SaleForecastService;

@Controller
public class SaleForecastController extends BaseController {
	@Autowired
	private SaleForecastService saleForecastService;
	@Autowired
	private SingleGridPanelService singleGridPanelService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveSaleForecast.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.saveSaleForecast(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteSaleForecast.action")  
	@ResponseBody 
	public Map<String, Object> deleteSaleForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.deleteSaleForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateSaleForecast.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.updateSaleForecastById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printSaleForecast.action")  
	@ResponseBody 
	public Map<String, Object> printSaleForecast(String caller, int id,String reportName,String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = saleForecastService.printSaleForecast(id, caller,reportName,condition);
		modelMap.put("success", true);
		modelMap.put("keyData",keys);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitSaleForecast.action")  
	@ResponseBody 
	public Map<String, Object> submitSaleForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.submitSaleForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitSaleForecast.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitSaleForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.resSubmitSaleForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditSaleForecast.action")  
	@ResponseBody 
	public Map<String, Object> auditSaleForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.auditSaleForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditSaleForecast.action")  
	@ResponseBody 
	public Map<String, Object> resAuditSaleForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.resAuditSaleForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/sale/SaleForecastChangedate.action")
	@ResponseBody
	public Map<String, Object> SaleForecastChange(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.saveSaleForecastChangedate(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 * 打开Mrp
	 */
	@RequestMapping("/scm/sale/openMrp.action")  
	@ResponseBody 
	public Map<String, Object> openMrb(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.openMrp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 关闭Mrp
	 */
	@RequestMapping("/scm/sale/CloseMrp.action")  
	@ResponseBody 
	public Map<String, Object> closeMrp(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.closeMrp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 更改预测数量
	 * */
	@RequestMapping("/scm/sale/UpdateForecastQty.action")
	@ResponseBody
	public Map<String, Object> UpdateForecastQty(String caller, String data ) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.UpdateForecastQty(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 短期预测 
	 * */
	@RequestMapping("/scm/sale/getGridConfig.action")
	@ResponseBody
	public Map<String, Object> getGridFields(String caller, String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GridPanel gridPanel = singleGridPanelService.getGridPanelByCaller(caller, condition, null, null, 1,false,"");
		modelMap.put("fields", gridPanel.getGridFields());
		//这里的columns里面添加了属性dbfind，方便进行dbfind操作。详见com.uas.erp.model.GridColumns的构造函数
		modelMap.put("columns", gridPanel.getGridColumns());
		//B2B里面每次dbfind都要重新查找dbfindsetgrid配置，实在麻烦，
		//所以在grid加载时，直接将dbfindsetgrid配置得到并传到前台
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("limits", gridPanel.getLimits());
		modelMap.put("date",saleForecastService.getShortConfig(condition));
		if(condition.equals("")){//表示是单表录入界面
			//为grid空白行设置一些默认值
		} else {//表示是单表显示界面
			modelMap.put("data", gridPanel.getDataString());
		}
		return modelMap;
	}
	/**
	 * 更新短期预测 
	 * */
	@RequestMapping("/scm/sale/updateShortForecast")
	@ResponseBody
	public Map<String,Object> updateShortForecast(String caller,String formStore, String param){
	    Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.updateShortForecast(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/saleforecastdataupdate.action")  
	@ResponseBody 
	public Map<String, Object> saleforecastdataupdate(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.saleforecastdataupdate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 分拆销售预测单
	 * */
	@RequestMapping("scm/sale/splitSaleForecast.action")
	@ResponseBody
	public Map<String, Object> splitSaleForecast(String formdata, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.splitSaleForecast(formdata, data);
		modelMap.put("success", true);
		return modelMap;
	}

}
