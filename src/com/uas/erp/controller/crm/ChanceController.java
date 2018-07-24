package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.GridPanel;
import com.uas.erp.service.common.SingleGridPanelService;
import com.uas.erp.service.crm.ChanceService;
@Controller
public class ChanceController {
	@Autowired
	private ChanceService chanceService;
	@Autowired
	private SingleGridPanelService singleGridPanelService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/crm/Chance/saveChance.action")  
	@ResponseBody 
	public Map<String, Object> save(  String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chanceService.saveChance( formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除ECN数据
	 * 包括ECN明细
	 */
	@RequestMapping("/crm/Chance/deleteChance.action")  
	@ResponseBody 
	public Map<String, Object> deleteChance( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chanceService.deleteChance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/crm/Chance/updateChance.action")  
	@ResponseBody 
	public Map<String, Object> update( String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chanceService.updateChance(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 分配任务执行人
	 * 改变状态
	 */
	@RequestMapping("crm/chanceTurnStatus.action")  
	@ResponseBody 
	public Map<String, Object> turnStatus(  String data,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chanceService.turnStatus(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 分配任务执行人
	 * 改变状态
	 */
	@RequestMapping("crm/chanceTurnEnd.action")  
	@ResponseBody 
	public Map<String, Object> turnEnd(String data,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chanceService.turnEnd(data,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 获取销售漏斗数据
	 */
	@RequestMapping("crm/funnel.action")  
	@ResponseBody 
	public Map<String, Object> funnel(String condition,String caller) {
		return chanceService.getFunnelData(condition,caller);
	}
	/**
	 * 提交
	 */
	@RequestMapping("/crm/Chance/submitChance.action")  
	@ResponseBody 
	public Map<String, Object> submitChance(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chanceService.submitChance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/crm/Chance/resSubmitChance.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitChance(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chanceService.resSubmitChance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/crm/Chance/auditChance.action")  
	@ResponseBody 
	public Map<String, Object> auditChance( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chanceService.auditChance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/crm/Chance/resAuditChance.action")  
	@ResponseBody 
	public Map<String, Object> resAuditChance( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chanceService.resAuditChance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	  @RequestMapping(value="/crm/Chance/singleGridPanel.action")
	  @ResponseBody
		public Map<String, Object> getGridFields(String caller, String condition){
			Map<String, Object> modelMap = new HashMap<String, Object>();			
			if(!condition.equals("")){
				condition=condition+" and ch_statu='已审核'";				
			}else{
				condition=" ch_statu='已审核'";
			}
			GridPanel gridPanel = singleGridPanelService.getGridPanelByCaller(caller, condition,1,200,null,false,"");
			modelMap.put("fields", gridPanel.getGridFields());
			//这里的columns里面添加了属性dbfind，方便进行dbfind操作。详见com.uas.erp.model.GridColumns的构造函数
			modelMap.put("columns", gridPanel.getGridColumns());
			//B2B里面每次dbfind都要重新查找dbfindsetgrid配置，实在麻烦，
			//所以在grid加载时，直接将dbfindsetgrid配置得到并传到前台
			modelMap.put("dbfinds", gridPanel.getDbfinds());
			modelMap.put("limits", gridPanel.getLimits());
			//modelMap.put("date",chanceService.getDateRange(condition));
			modelMap.put("data", gridPanel.getDataString());
			modelMap.put("data2", chanceService.haveAllstatus(gridPanel.getDataString(), caller));
			return modelMap;
		}
	
}
