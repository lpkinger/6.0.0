package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.VerifyApplyDetailService;

@Controller
public class VerifyApplyDetailController {

	@Autowired
	VerifyApplyDetailService verifyApplyDetailService;

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/qc/deleteVerifyApplyDetail.action")
	@ResponseBody
	public Map<String, Object> deleteVerifyApplyDetail(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailService.deleteVerifyApplyDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除2
	 * 
	 * 包括2个明细表
	 */
	@RequestMapping("/scm/qc/deleteVerifyApplyDetail2.action")
	@ResponseBody
	public Map<String, Object> deleteVerifyApplyDetail2(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailService.deleteVerifyApplyDetail2(id, caller);
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
	@RequestMapping("/scm/qc/updateVerifyApplyDetail.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailService.updateVerifyApplyDetailById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param1
	 *            grid1数据
	 * @param param2
	 *            grid2数据
	 */
	@RequestMapping("/scm/qc/updateVerifyApplyDetail2.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String param2, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailService.updateVerifyApplyDetailById2(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/scm/qc/printVerifyApplyDetail.action")
	@ResponseBody
	public Map<String, Object> printVerifyApplyDetail(int id, String reportName, String condition, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = verifyApplyDetailService.printVerifyApplyDetail(id, reportName, condition, caller);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 转mrb yaozx13-08-14
	 */
	@RequestMapping("/scm/qc/turnMrb.action")
	@ResponseBody
	public Map<String, Object> turnMrb(int id, String code, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailService.turnMrb(id, code, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转生产品质异常单 madan 2014-8-1 14:56:12
	 */
	@RequestMapping("/scm/qc/turnQualityYC.action")
	@ResponseBody
	public Map<String, Object> turnQualityYC(int id, String code, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int mqid = verifyApplyDetailService.turnMakeQualityYC(id, code, caller);
		modelMap.put("id", mqid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/qc/auditVerifyApplyDetail.action")
	@ResponseBody
	public Map<String, Object> auditVerifyApplyDetail(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailService.auditVerifyApplyDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/qc/resAuditVerifyApplyDetail.action")
	@ResponseBody
	public Map<String, Object> resAuditVerifyApplyDetail(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailService.resAuditVerifyApplyDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 采购检验单：清除已经抓取的检验项目
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/scm/qc/catchProject.action")
	@ResponseBody
	public Map<String, Object> catchProject(int id, int prid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailService.catchProject(id, prid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 预收冲应收界面：抓取应收发票
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/scm/qc/cleanProject.action")
	@ResponseBody
	public Map<String, Object> cleanProject(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailService.cleanProject(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 分装单据(明细)
	 */
	@RequestMapping("/scm/purchase/SubpackageDetail.action")
	@ResponseBody
	public Map<String, Object> SubpackageDetail(int id, double tqty) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String logString = verifyApplyDetailService.SubpackageDetail(id, tqty);
		modelMap.put("success", true);
		modelMap.put("log", logString);
		return modelMap;
	}

	/**
	 * 清除分装明细(明细)
	 */
	@RequestMapping("/scm/purchase/ClearSubpackageDetail.action")
	@ResponseBody
	public Map<String, Object> ClearSubpackageDetail(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String logString = verifyApplyDetailService.ClearSubpackageDetail(id);
		modelMap.put("success", true);
		modelMap.put("log", logString);
		return modelMap;
	}

	/**
	 * 条码打印(明细)
	 */
	@RequestMapping("/scm/purchase/PrintBarDetail.action")
	@ResponseBody
	public Map<String, Object> PrintBarDetail(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String logString = verifyApplyDetailService.PrintBarDetail(id);
		modelMap.put("success", true);
		modelMap.put("log", logString);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/qc/submitVerifyApplyDetail.action")
	@ResponseBody
	public Map<String, Object> submitVerifyApplyDetail(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailService.submitVerifyApplyDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/qc/resSubmitVerifyApplyDetail.action")
	@ResponseBody
	public Map<String, Object> resSubmitVerifyApplyDetail(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailService.resSubmitVerifyApplyDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批准
	 */
	@RequestMapping("/scm/qc/checkVerifyApplyDetail.action")
	@ResponseBody
	public Map<String, Object> approveVerifyApplyDetail(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailService.approveVerifyApplyDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反批准
	 */
	@RequestMapping("/scm/qc/resCheckVerifyApplyDetail.action")
	@ResponseBody
	public Map<String, Object> resApproveVerifyApplyDetail(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailService.resApproveVerifyApplyDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 明细数量修改
	 * */
	@RequestMapping("scm/qc/updateQty.action")
	@ResponseBody
	public Map<String, Object> updateQty(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailService.updateQty(data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 明细仓库修改
	 * */
	@RequestMapping("scm/qc/updateWhCodeInfo.action")
	@ResponseBody
	public Map<String, Object> updateWhCodeInfo(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailService.updateWhCodeInfo(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转物料品质异常联络单
	 * 
	 * @author madan 2016-06-08 14:28:08
	 */
	@RequestMapping("/scm/qc/turnProdAbnormal.action")
	@ResponseBody
	public Map<String, Object> turnProdAbnormal(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", verifyApplyDetailService.turnProdAbnormal(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转8D报告
	 */
	@RequestMapping("/scm/qc/turnT8DReport.action")
	@ResponseBody
	public Map<String, Object> turnT8DReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", verifyApplyDetailService.turnT8DReport(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	/*
	 *  maz  再次送检 2017090392 17-10-30
	 */
	@RequestMapping("/scm/qc/InspectAgain.action")
	@ResponseBody
	public Map<String, Object> InspectAgain(String ve_code,int ve_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", verifyApplyDetailService.InspectAgain(ve_code,ve_id));
		modelMap.put("success", true);
		return modelMap;
	}
}
