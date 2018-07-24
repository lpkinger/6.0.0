package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ComplaintRecordsService;

@Controller
public class ComplaintRecordsController {
	@Autowired
	private ComplaintRecordsService complaintRecordsService;
	/**
	 * 保存ComplaintRecords
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/scm/qc/saveComplaintRecords.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintRecordsService.saveComplaintRecords(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/qc/updateComplaintRecords.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintRecordsService.updateComplaintRecordsById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/qc/deleteComplaintRecords.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintRecordsService.deleteComplaintRecords(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/qc/submitComplaintRecords.action")  
	@ResponseBody 
	public Map<String, Object> submitComplaintRecords(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintRecordsService.submitComplaintRecords(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/qc/resSubmitComplaintRecords.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitComplaintRecords(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintRecordsService.resSubmitComplaintRecords(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/qc/auditComplaintRecords.action")  
	@ResponseBody 
	public Map<String, Object> auditComplaintRecords(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintRecordsService.auditComplaintRecords(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/qc/resAuditComplaintRecords.action")  
	@ResponseBody 
	public Map<String, Object> resAuditComplaintRecords(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintRecordsService.resAuditComplaintRecords(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改ComplaintRecords 信息
	 * @param val1   投诉处理结果
	 * @param val2   责任人
	 * @param val3   责任部门
	 */
	@RequestMapping("/scm/qc/updateComplaint.action")  
	@ResponseBody
	public Map<String, Object> updateUU(Integer id, String val1, String val2, String val3, String val4,String val0, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintRecordsService.updateComplaint(id, val1, val2, val3,val4, val0,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 结案
	 */
	@RequestMapping("/scm/qc/endComplaintRecords.action")
	@ResponseBody
	public Map<String, Object> endComplaintRecords(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintRecordsService.endComplaintRecords(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/scm/qc/resEndComplaintRecords.action")
	@ResponseBody
	public Map<String, Object> resEndComplaintRecords(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintRecordsService.resEndComplaintRecords(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/qc/printComplaintRecords.action")  
	@ResponseBody 
	public Map<String, Object> print(int id,String reportName,String condition, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = complaintRecordsService.printComplaintRecords(id, reportName,condition, caller);
		modelMap.put("success", true);
		modelMap.put("keyData",keys);
		return modelMap;
	}
}
