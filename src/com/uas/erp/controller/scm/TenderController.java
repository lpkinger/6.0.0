package com.uas.erp.controller.scm;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.service.scm.TenderService;

@Controller
public class TenderController {
	
	@Autowired
	TenderService tenderService;
	
	@RequestMapping(value="/scm/purchase/getTenderList.action",produces="application/json;charset=UTF-8;")  
	@ResponseBody 
	public Map<String, Object> getTenderList(String page,String limit,String search,String date,String status) {
		return tenderService.getTenderList(page, limit, search, date,status);
	}
	
	@RequestMapping(value="scm/purchase/getTender.action",produces="application/json;charset=UTF-8;")  
	@ResponseBody 
	public Map<String, Object> getTender(String id) {
		Map<String, Object> modelMap =  tenderService.getTender(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 是否进入审批流
	 */
	@RequestMapping("/scm/purchase/isSubmit.action")
	@ResponseBody
	public Map<Object, Object> isSubmit(String caller) {
		Map<Object, Object> modelMap = new HashMap<Object, Object>();
		modelMap.put("submit",tenderService.isSubmit(caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存、更新、发布招标单
	 */
	@RequestMapping("/scm/purchase/saveorPublicTender.action")
	@ResponseBody
	public Map<Object, Object> saveTender(HttpServletRequest request, String formStore, String param,String param1,String caller,int isPublish) {
		Map<Object, Object> modelMap = tenderService.saveorPublicTender(caller, formStore, param,param1,isPublish);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 发布招标单
	 */
	@RequestMapping("/scm/purchase/publicTender.action")
	@ResponseBody
	public Map<Object, Object> publicTender(Integer id,String caller) {
		Map<Object, Object> modelMap = new HashMap<Object, Object>();
		tenderService.publicTender(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除招标单
	 */
	@RequestMapping("/scm/purchase/deleteTender.action")
	@ResponseBody
	public Map<String, Object> deleteTender(Integer id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderService.deleteTender(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除招标单产品明细
	 */
	@RequestMapping("/scm/purchase/deleteProd.action")
	@ResponseBody
	public Map<String, Object> deleteProd(Integer tenderProdId) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderService.deleteProd(tenderProdId);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除对应投标单
	 */
	@RequestMapping("/scm/purchase/removeSaleTender.action")
	@ResponseBody
	public Map<String, Object> removeSaleTender(Integer id, Long vendUU, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderService.removeSaleTender(id, vendUU, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value="scm/purchase/getTenderEstimate.action",produces="application/json;charset=UTF-8;")  
	@ResponseBody 
	public Map<String, Object> getTenderEstimate(String id) {
		Map<String, Object> modelMap = tenderService.getTenderEstimate(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping(value="/scm/sale/getTenderList.action",produces="application/json;charset=UTF-8;")  
	@ResponseBody 
	public Map<String, Object> getTenderList(String page,String limit,String search,String date) {
		return tenderService.getTenderList(page, limit, search, date);
	}
	
	@RequestMapping(value="/scm/sale/getTenderCustList.action",produces="application/json;charset=UTF-8;")  
	@ResponseBody 
	public Map<String, Object> getTenderCustList(String page,String limit,String search,String date,String status) {
		return tenderService.getTenderCustList(page, limit, search, date,status);
	}
	
	@RequestMapping(value="scm/sale/getTenderPublic.action",produces="application/json;charset=UTF-8;")  
	@ResponseBody 
	public Map<String, Object> getTenderPublic(String id) {
		Map<String, Object> modelMap = tenderService.getTenderPublic(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping(value="scm/sale/addTenderItems.action",produces="application/json;charset=UTF-8;")  
	@ResponseBody 
	public Map<String, Object> addTenderItems(String id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", tenderService.addTenderItems(id));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value="scm/sale/getTenderSubmission.action",produces="application/json;charset=UTF-8;")  
	@ResponseBody 
	public Map<String, Object> getTenderSubmission(String id) {
		Map<String, Object> modelMap = tenderService.getTenderSubmission(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/scm/sale/saveSaleTender.action")
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request, String formStore, String enBaseInfo,String param, String attachs,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderService.saveSaleTender(caller, formStore, enBaseInfo,param,attachs);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitSaleTender.action")
	@ResponseBody
	public Map<String, Object> submitSaleTender(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderService.submitSaleTender(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitSaleTender.action")
	@ResponseBody
	public Map<String, Object> resSubmitSaleTender(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderService.resSubmitSaleTender(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitTender.action")
	@ResponseBody
	public Map<String, Object> resSubmitTender(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderService.resSubmitTender(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditSaleTender.action")
	@ResponseBody
	public Map<String, Object> auditSaleTender(Integer id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderService.auditSaleTender(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditSaleTender.action")
	@ResponseBody
	public Map<String, Object> resAuditSaleTender(Integer id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderService.resAuditSaleTender(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存评标单(甲供料)
	 */
	@RequestMapping("/scm/purchase/saveEstimateTender.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderService.saveEstimateTender(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitEstimateTender.action")
	@ResponseBody
	public Map<String, Object> submitEstimateTender(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderService.submitEstimateTender(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitEstimateTender.action")
	@ResponseBody
	public Map<String, Object> resSubmitEstimateTender( int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderService.resSubmitEstimateTender(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditEstimateTender.action")
	@ResponseBody
	public Map<String, Object> auditEstimateTender(Integer id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderService.auditEstimateTender(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转采购合同
	 */
	@RequestMapping("/scm/purchase/tenderTurnPurchase.action")
	@ResponseBody
	public Map<String, Object> turnPurchase(String caller,String fromStore,String param,String vendUUs) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Long> VendUUs =  FlexJsonUtil.fromJsonArray(vendUUs, Long.class);
		modelMap.put("msg", tenderService.turnPurchase(caller, fromStore, param,VendUUs));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取流程节点
	 */
	@RequestMapping(value="/scm/purchase/getJProcessByForm.action")
	@ResponseBody
	public Map<String,Object> getJProcessByForm(String finds){
		Map<String, Object> modelMap = tenderService.getJProcessByForm(finds);
		modelMap.put("success", true);
		return modelMap;
	}
}
