package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.KBIbillService;

@Controller
public class KBIbillController {
	@Autowired
	private KBIbillService kbIbillService;

	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/hr/kbi/saveKBIbill.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbIbillService.saveKBIbill(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/kbi/deleteKBIbill.action")
	@ResponseBody
	public Map<String, Object> deleteKBIbill(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbIbillService.deleteKBIbill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/hr/kbi/updateKBIbill.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbIbillService.updateKBIbillById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/kbi/submitKBIbill.action")
	@ResponseBody
	public Map<String, Object> submitKBIbill(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbIbillService.submitKBIbill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/kbi/resSubmitKBIbill.action")
	@ResponseBody
	public Map<String, Object> resSubmitKBIbill(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbIbillService.resSubmitKBIbill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/kbi/auditKBIbill.action")
	@ResponseBody
	public Map<String, Object> auditKBIbill(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbIbillService.auditKBIbill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/kbi/resAuditKBIbill.action")
	@ResponseBody
	public Map<String, Object> resAuditKBIbill(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbIbillService.resAuditKBIbill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 评估单批量处理
	 */
	@RequestMapping("/hr/kbi/endKBIbill.action")
	@ResponseBody
	public Map<String, Object> endKBIbill(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbIbillService.endKBIBill(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/hr/kbi/singleGridPanel.action")
	@ResponseBody
	public Map<String, Object> getGridFields(String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (condition == null || condition == "") {
			modelMap.put("data", "");
		} else {
			modelMap.put("data", kbIbillService.showKbi(condition));
		}
		/*
		 * if(condition.contains("ma_date")){ String[]
		 * contion=condition.split("AND");
		 * conditionDate=condition.split("AND")[0]; if(contion.length>1){
		 * condition=contion[1]; }else{ condition=null; } }
		 * if(condition==null||condition==""){ condition="mr_status='已审核'";
		 * }else{ condition+=" AND mr_status='已审核'"; }
		 */
		/*
		 * GridPanel gridPanel =
		 * singleGridPanelService.getGridPanelByCaller(caller, condition,
		 * caller, null, null, 1); modelMap.put("fields",
		 * gridPanel.getGridFields());
		 * //这里的columns里面添加了属性dbfind，方便进行dbfind操作。详见com
		 * .uas.erp.model.GridColumns的构造函数 modelMap.put("columns",
		 * gridPanel.getGridColumns());
		 * //B2B里面每次dbfind都要重新查找dbfindsetgrid配置，实在麻烦，
		 * //所以在grid加载时，直接将dbfindsetgrid配置得到并传到前台 modelMap.put("dbfinds",
		 * gridPanel.getDbfinds()); modelMap.put("limits",
		 * gridPanel.getLimits());
		 */
		// modelMap.put("date",chanceService.getDateRange(condition));
		modelMap.put("fields", kbIbillService.getGridFields());
		modelMap.put("columns", kbIbillService.getGridColumns());

		// modelMap.put("data2",
		// meetingRoomService.showapply(gridPanel.getDataString(),
		// caller,conditionDate));
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/kbi/getKeys.action")
	@ResponseBody
	public Map<String, Object> getKeys(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("data", kbIbillService.getKeys());
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/kbi/getAssessValue.action")
	@ResponseBody
	public Map<String, Object> getAssessValue(String caller, String key) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("data", kbIbillService.getAssessValue(key));
		return modelMap;
	}

}
