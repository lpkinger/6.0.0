package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.EvaluationService;

@Controller
public class EvaluationController extends BaseController {
	@Autowired
	private EvaluationService evaluationService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/sale/saveEvaluation.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param1, String param2, String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.saveEvaluation(formStore, param1, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/sale/deleteEvaluation.action")
	@ResponseBody
	public Map<String, Object> deleteEvaluation(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.deleteEvaluation(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除明细行
	 * 
	 * @author madan 2017-05-22 16:15:09
	 */
	@RequestMapping("/scm/sale/deleteEvaluationDetail.action")
	@ResponseBody
	public Map<String, Object> deletePreSaleForecastDetail(String id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.deleteEvaluationDetail(id, caller);
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
	@RequestMapping("/scm/sale/updateEvaluation.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param1, String param2, String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.updateEvaluationById(formStore, param1, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printEvaluation.action")
	@ResponseBody
	public Map<String, Object> printEvaluation(int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.printEvaluation(id, reportName, condition);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitEvaluation.action")
	@ResponseBody
	public Map<String, Object> submitEvaluation(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.submitEvaluation(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitEvaluation.action")
	@ResponseBody
	public Map<String, Object> resSubmitEvaluation(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.resSubmitEvaluation(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditEvaluation.action")
	@ResponseBody
	public Map<String, Object> auditEvaluation(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.auditEvaluation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditEvaluation.action")
	@ResponseBody
	public Map<String, Object> resAuditEvaluation(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.resAuditEvaluation(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 禁用
	 */
	@RequestMapping("/scm/sale/bannedEvaluation.action")
	@ResponseBody
	public Map<String, Object> banned(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.bannedEvaluation(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反禁用
	 */
	@RequestMapping("/scm/sale/resBannedEvaluation.action")
	@ResponseBody
	public Map<String, Object> resBanned(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.resBannedEvaluation(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 报价成本计算
	 */
	@RequestMapping("/scm/sale/bomoffercost.action")
	@ResponseBody
	public Map<String, Object> bomOfferCost(int ev_id, int bo_id, String pr_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.calBOMOfferCost(ev_id, bo_id, pr_code);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 参考材料成本计算
	 */
	@RequestMapping("/scm/sale/bomcost.action")
	@ResponseBody
	public Map<String, Object> bomCost(int ev_id, int bo_id, String pr_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.calBOMCost(ev_id, bo_id, pr_code);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 导入建立BOM的产品
	 */
	@RequestMapping("/scm/sale/bominsert.action")
	@ResponseBody
	public Map<String, Object> bomInsert(int ev_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.bomInsert(ev_id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量计算产品BOM成本
	 */
	@RequestMapping("/scm/sale/bomvastcost.action")
	@ResponseBody
	public Map<String, Object> bomVastCost(int ev_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.bomVastCost(ev_id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转报价
	 */
	@RequestMapping("/scm/evaluation/turnQuotation.action")
	@ResponseBody
	public Map<String, Object> turnQuotation(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int quid = evaluationService.turnQuotation(id);
		modelMap.put("id", quid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 清除报价材料明细
	 */
	@RequestMapping("/scm/sale/clearbomoffer.action")
	@ResponseBody
	public Map<String, Object> clearBomOffer(int ev_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationService.clearBomOffer(ev_id);
		modelMap.put("success", true);
		return modelMap;
	}
}
