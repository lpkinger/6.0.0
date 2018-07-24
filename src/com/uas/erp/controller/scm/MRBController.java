package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.MRBService;

@Controller
public class MRBController {
	@Autowired
	private MRBService mRBService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/qc/saveMRB.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRBService.saveMRB(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/qc/deleteMRB.action")  
	@ResponseBody 
	public Map<String, Object> deleteMRB(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRBService.deleteMRB(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param1 grid1数据
	 * @param param2 grid2数据
	 */
	@RequestMapping("/scm/qc/updateMRB.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String param2, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRBService.updateMRBById(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/qc/printMRB.action")  
	@ResponseBody 
	public Map<String, Object> printMRB(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRBService.printMRB(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 */
	@RequestMapping("/scm/qc/auditMRB.action")  
	@ResponseBody 
	public Map<String, Object> auditMake(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRBService.auditMRB(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/qc/resAuditMRB.action")  
	@ResponseBody 
	public Map<String, Object> resAuditMake(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRBService.resAuditMRB(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/qc/submitMRB.action")  
	@ResponseBody 
	public Map<String, Object> submitMRB(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRBService.submitMRB(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/qc/resSubmitMRB.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitMRB(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRBService.resSubmitMRB(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 批准
	 */
	@RequestMapping("/scm/qc/checkMRB.action")  
	@ResponseBody 
	public Map<String, Object> approveMRB(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRBService.approveMRB(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反批准
	 */
	@RequestMapping("/scm/qc/resCheckMRB.action")  
	@ResponseBody 
	public Map<String, Object> resApproveMRB(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRBService.resApproveMRB(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
