package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.PreProductBatchService;

@Controller
public class PreProductBatchController extends BaseController {
	@Autowired
	private PreProductBatchService preProductBatchService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/product/savePreProductBatch.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductBatchService.savePreProductBatch(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/product/deletePreProductBatch.action")
	@ResponseBody
	public Map<String, Object> deletePreProductBatch(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductBatchService.deletePreProductBatch(id, caller);
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
	@RequestMapping("/scm/product/updatePreProductBatch.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductBatchService.updatePreProductBatchById(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitPreProductBatch.action")
	@ResponseBody
	public Map<String, Object> submitPreProductBatch(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductBatchService.submitPreProductBatch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitPreProductBatch.action")
	@ResponseBody
	public Map<String, Object> resSubmitPreProductBatch(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductBatchService.resSubmitPreProductBatch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/product/auditPreProductBatch.action")
	@ResponseBody
	public Map<String, Object> auditPreProductBatch(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = preProductBatchService.auditPreProductBatch(id, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditPreProductBatch.action")
	@ResponseBody
	public Map<String, Object> resAuditPreProductBatch(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductBatchService.resAuditPreProductBatch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 *  抓取编号
	 */
	@RequestMapping("/scm/product/CatchProdCode.action")
	@ResponseBody
	public Map<String, Object> catchProdCode(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductBatchService.catchProdCode(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
