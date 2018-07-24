package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.CommonService;
import com.uas.erp.service.scm.VendorPerformanceAssessService;

/**
 * 供应商绩效考核的controller 对应前台通用的jsp:jsps/scm//purchase/vendorPerformanceAssess.jsp
 */
@Controller
public class VendorPerformanceAssessController {
	
	@Autowired
	private VendorPerformanceAssessService vendorPerformanceAssessService;

	/**
	 * 保存
	 */
	@RequestMapping("/scm/purchase/saveVPA.action")
	@ResponseBody
	public Map<String, Object> save(String caller,  String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorPerformanceAssessService.saveVPA(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/scm/purchase/deleteVPA.action")
	@ResponseBody
	public Map<String, Object> deleteCommon(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorPerformanceAssessService.deleteVPA(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/scm/purchase/updateVPA.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorPerformanceAssessService.updateVPA(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitVPA.action")
	@ResponseBody
	public Map<String, Object> submitCommon(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorPerformanceAssessService.submitVPA(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitVPA.action")
	@ResponseBody
	public Map<String, Object> resSubmitCommon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorPerformanceAssessService.resSubmitVPA(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditVPA.action")
	@ResponseBody
	public Map<String, Object> auditCommon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorPerformanceAssessService.auditVPA(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditVPA.action")
	@ResponseBody
	public Map<String, Object> resAuditCommon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorPerformanceAssessService.resAuditVPA(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拿到id
	 *//*
	@RequestMapping("/common/getCommonId.action")
	@ResponseBody
	public Map<String, Object> getId(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", commonService.getId(caller));
		modelMap.put("success", true);
		return modelMap;
	}

	*//**
	 * 拿到id 通过序列名称
	 *//*
	@RequestMapping("/common/getSequenceId.action")
	@ResponseBody
	public Map<String, Object> getSequenceId(String caller, String seqname) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", commonService.getSequenceid(seqname));
		modelMap.put("success", true);
		return modelMap;
	}

	*//**
	 * 禁用
	 *//*
	@RequestMapping("/common/bannedCommon.action")
	@ResponseBody
	public Map<String, Object> bannedCurrencys(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.bannedCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	*//**
	 * 反禁用
	 *//*
	@RequestMapping("/common/resBannedCommon.action")
	@ResponseBody
	public Map<String, Object> resBannedCurrencys(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.resBannedCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	*//**
	 * 結案
	 *//*
	@RequestMapping("/common/endCommon.action")
	@ResponseBody
	public Map<String, Object> endCurrencys(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.endCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	*//**
	 * 反結案
	 *//*
	@RequestMapping("/common/resEndCommon.action")
	@ResponseBody
	public Map<String, Object> resEndCurrencys(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.resEndCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	*//**
	 * 过账
	 *//*
	@RequestMapping("/common/postCommon.action")
	@ResponseBody
	public Map<String, Object> postCommon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.postCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	*//**
	 * 确认,操作是确认从B2B下载来的PO，确认除了更新确认状态，还要更新相关字段，eg:录入人，客户/供应商编号等
	 *//*
	@RequestMapping("/common/ConfirmCommon.action")
	@ResponseBody
	public Map<String, Object> onConfirmCommon(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.confirmCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/common/getCountByTable.action")
	@ResponseBody
	public Map<String, Object> getCountByTable(String caller, String condition, String tablename) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int count = commonService.getCountByTable(condition, tablename);
		modelMap.put("count", count);
		modelMap.put("success", true);
		return modelMap;
	}

	// 打印
	@RequestMapping("/common/printCommon.action")
	@ResponseBody
	public Map<String, Object> printCommon(String caller, int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = commonService.printCommon(id, caller, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
	
	*//**
	 * 更新(非在录入状态下更新主表信息)
	 *//*
	@RequestMapping("/oa/form/modify.action")
	@ResponseBody
	public Map<String, Object> modify(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.modify(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	*//**
	 * 非在录入状态更新明细数据
	 *//*
	@RequestMapping(value = "/oa/modifyDetail.action")
	@ResponseBody
	public Map<String, Object> modifyGrid(String caller,String param,String log) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.modifyDetail(caller, param,log);
		modelMap.put("success", true);
		return modelMap;
	}
	*//**
	 * 获得打印header及bottom
	 *//*
	@RequestMapping(value = "/common/getPrintSet.action")
	@ResponseBody
	public Map<String, Object> getPrintSet() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", commonService.getPrintSet());
		modelMap.put("success", true);
		return modelMap;
	}*/
}
