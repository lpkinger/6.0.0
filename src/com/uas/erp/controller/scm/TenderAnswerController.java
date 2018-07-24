package com.uas.erp.controller.scm;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.TenderAnswerService;

@Controller
public class TenderAnswerController {
	
	@Autowired
	TenderAnswerService tenderAnswerService;
	
	
	/**
	 * 获取招标问题列表
	 */
	@RequestMapping(value="/scm/purchase/getTenderQuestionList.action",produces="application/json;charset=UTF-8;")  
	@ResponseBody 
	public Map<String, Object> getTenderQuestionList(String page,String limit,String search,String date,String status) {
		return tenderAnswerService.getTenderQuestionList(page, limit, search, date,status);
	}
	
	/**
	 * 获取招标问题详情
	 */
	@RequestMapping(value="scm/purchase/getTenderQuestion.action",produces="application/json;charset=UTF-8;")  
	@ResponseBody 
	public Map<String, Object> getTenderQuestion(String id) {
		Map<String, Object> modelMap =  tenderAnswerService.getTenderQuestion(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取招标问题汇总
	 */
	@RequestMapping(value="scm/purchase/getQuestionsByTender.action",produces="application/json;charset=UTF-8;")  
	@ResponseBody 
	public Map<String, Object> getQuestionsByTender(String tenderCode) {
		Map<String, Object> modelMap =  tenderAnswerService.getQuestionsByTender(tenderCode);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/scm/purchase/saveTenderAnswer.action")
	@ResponseBody
	public Map<String, Object> save(String caller,String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderAnswerService.saveTenderAnswer(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新
	 */
	@RequestMapping("/scm/purchase/updateTenderAnswer.action")
	@ResponseBody
	public Map<String, Object> update(String caller,String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderAnswerService.updateTenderAnswer(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/purchase/deleteTenderAnswer.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderAnswerService.deleteTenderAnswer(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitTenderAnswer.action")
	@ResponseBody
	public Map<String, Object> submitTenderAnswer(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderAnswerService.submitTenderAnswer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitTenderAnswer.action")
	@ResponseBody
	public Map<String, Object> resSubmitTenderAnswer(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderAnswerService.resSubmitTenderAnswer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditTenderAnswer.action")
	@ResponseBody
	public Map<String, Object> auditTenderAnswer(Integer id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderAnswerService.auditTenderAnswer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditTenderAnswer.action")
	@ResponseBody
	public Map<String, Object> resAuditTenderAnswer(Integer id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderAnswerService.resAuditTenderAnswer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
