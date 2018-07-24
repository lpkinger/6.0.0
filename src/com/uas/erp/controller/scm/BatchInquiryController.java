package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.BatchInquiryService;

@Controller
public class BatchInquiryController extends BaseController {
	@Autowired
	private BatchInquiryService batchInquiryService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/purchase/saveBatchInquiry.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param1,String param2, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchInquiryService.saveBatchInquiry(formStore, param1,param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/purchase/deleteBatchInquiry.action")
	@ResponseBody
	public Map<String, Object> deleteBatchInquiry(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchInquiryService.deleteBatchInquiry(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/purchase/updateBatchInquiry.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param1,String param2, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchInquiryService.updateBatchInquiryById(formStore, param1, param2,caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitBatchInquiry.action")
	@ResponseBody
	public Map<String, Object> submitBatchInquiry(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchInquiryService.submitBatchInquiry(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitBatchInquiry.action")
	@ResponseBody
	public Map<String, Object> resSubmitBatchInquiry(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchInquiryService.resSubmitBatchInquiry(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditBatchInquiry.action")
	@ResponseBody
	public Map<String, Object> auditBatchInquiry(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchInquiryService.auditBatchInquiry(id, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditBatchInquiry.action")
	@ResponseBody
	public Map<String, Object> resAuditBatchInquiry(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchInquiryService.resAuditBatchInquiry(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}



}
