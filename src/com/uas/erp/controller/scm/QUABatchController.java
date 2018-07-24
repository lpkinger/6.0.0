package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.QUABatchService;

@Controller
public class QUABatchController extends BaseController {
	@Autowired
	private QUABatchService QUABatchService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/reserve/saveQUABatch.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		QUABatchService.saveQUABatch(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/reserve/updateQUABatch.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		QUABatchService.updateQUABatchById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/reserve/deleteQUABatch.action")  
	@ResponseBody 
	public Map<String, Object> deleteSaleChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		QUABatchService.deleteQUABatch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/reserve/submitQUABatch.action")  
	@ResponseBody 
	public Map<String, Object> submitQUABatch(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		QUABatchService.submitQUABatch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/reserve/resSubmitQUABatch.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitQUABatch(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		QUABatchService.resSubmitQUABatch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/reserve/auditQUABatch.action")  
	@ResponseBody 
	public Map<String, Object> auditQUABatch(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		QUABatchService.auditQUABatch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/reserve/resAuditQUABatch.action")  
	@ResponseBody 
	public Map<String, Object> resAuditQUABatch(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		QUABatchService.resAuditQUABatch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
