package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.pm.MRPDataService;

@Controller
public class MRPDataController extends BaseController {
	@Autowired
	private MRPDataService MRPDataService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/make/saveMRPData.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
		MRPDataService.saveMRPData(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteMRPData.action")  
	@ResponseBody 
	public Map<String, Object> deleteMRPData(String caller, int id) {
	   Map<String, Object> modelMap = new HashMap<String, Object>();
		MRPDataService.deleteMRPData(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/make/updateMRPData.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MRPDataService.updateMRPDataById(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitMRPData.action")  
	@ResponseBody 
	public Map<String, Object> submitMRPData(String caller, int id) {			
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MRPDataService.submitMRPData(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitMRPData.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitMRPData(String caller, int id) {			
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MRPDataService.resSubmitMRPData(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditMRPData.action")  
	@ResponseBody 
	public Map<String, Object> auditMRPData(String caller, int id) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MRPDataService.auditMRPData(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditMRPData.action")  
	@ResponseBody 
	public Map<String, Object> resAuditMRPData(String caller, int id) {			
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MRPDataService.resAuditMRPData(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/mrp/updateFieldData.action")
	@ResponseBody
	public Map<String, Object> updateData(String caller,
			String data, String field, String keyField, String keyValue) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MRPDataService.updateFieldData(caller, data, field, keyField, keyValue);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/mrp/getMrpData.action")
	@ResponseBody
	public Map<String, Object> getMrpData( String caller,
			String condition, int page, int start, int limit) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		JSONObject obj = MRPDataService.getMrpData(caller,condition, page,
				start, limit);
		modelMap.put("toalCount", obj.get("totalCount"));
		modelMap.put("data", obj.get("data"));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/mrp/getMRPThrowConfig.action")
	@ResponseBody
	public Map<String, Object> getMRPThrowConfig(
			String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GridPanel panel = MRPDataService.getMRPThrowConfig(condition,
				caller);
		modelMap.put("fields", panel.getGridFields());
		modelMap.put("columns", panel.getGridColumns());
		modelMap.put("success", true);
		return modelMap;
	}
}
