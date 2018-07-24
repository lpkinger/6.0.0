package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fs.CustCreditRatingApplyService;

@Controller
public class CustCreditRatingApplyController extends BaseController {
	@Autowired
	private CustCreditRatingApplyService custCreditRatingApplyService;

	/**
	 * 保存
	 */
	@RequestMapping("/fs/credit/saveCustCreditRatingApply.action")
	@ResponseBody
	public Map<String, Object> saveCustCreditRatingApply(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custCreditRatingApplyService.saveCustCreditRatingApply(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fs/credit/updateCustCreditRatingApply.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custCreditRatingApplyService.updateCustCreditRatingApply(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/credit/deleteCustCreditRatingApply.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custCreditRatingApplyService.deleteCustCreditRatingApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/credit/submitCustCreditRatingApply.action")
	@ResponseBody
	public Map<String, Object> submitcustCreditRatingApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custCreditRatingApplyService.submitCustCreditRatingApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/credit/resSubmitCustCreditRatingApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitcustCreditRatingApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custCreditRatingApplyService.resSubmitCustCreditRatingApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/credit/auditCustCreditRatingApply.action")
	@ResponseBody
	public Map<String, Object> auditcustCreditRatingApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custCreditRatingApplyService.auditCustCreditRatingApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/credit/resAuditCustCreditRatingApply.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custCreditRatingApplyService.resAuditCustCreditRatingApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取display
	 */
	@RequestMapping("/fs/credit/getDisplay.action")
	@ResponseBody
	public Map<String, Object> getDisplay(String caller, Integer craid, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("display", custCreditRatingApplyService.getDisplay(caller, craid, type));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存
	 */
	@RequestMapping("/fs/credit/saveCustCreditTargets.action")
	@ResponseBody
	public Map<String, Object> saveCustCreditTargets(String datas) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custCreditRatingApplyService.saveCustCreditTargets(datas);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 测算
	 */
	@RequestMapping("/fs/credit/MeasureScore.action")
	@ResponseBody
	public Map<String, Object> MeasureScore(int craid, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custCreditRatingApplyService.MeasureScore(craid, type);
		modelMap.put("success", true);
		return modelMap;
	}
}
