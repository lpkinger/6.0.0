package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.CommonService;

/**
 * 通用的controller 对应前台通用的jsp:jsps/common/commonPage.jsp
 */
@Controller
public class CommonController {
	@Autowired
	private CommonService commonService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/common/saveCommon.action")
	@ResponseBody
	public Map<String, Object> save(String caller,  String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", commonService.saveCommon(caller, formStore, param));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/common/deleteCommon.action")
	@ResponseBody
	public Map<String, Object> deleteCommon(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.deleteCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/common/updateCommon.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.updateCommonById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印采购单
	 */
	/*
	 * @RequestMapping("/common/printCommon.action")
	 * 
	 * @ResponseBody public Map<String, Object> printCommon(
	 * String caller, int id) { String language =
	 * (String)session.getAttribute("language"); Employee employee =
	 * (Employee)session.getAttribute("employee"); Map<String, Object> modelMap
	 * = new HashMap<String, Object>(); commonService.printCommon(caller, id);
	 * modelMap.put("success", true); return modelMap; }
	 */
	/**
	 * 提交
	 */
	@RequestMapping("/common/submitCommon.action")
	@ResponseBody
	public Map<String, Object> submitCommon(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.submitCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/common/resSubmitCommon.action")
	@ResponseBody
	public Map<String, Object> resSubmitCommon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.resSubmitCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/common/auditCommon.action")
	@ResponseBody
	public Map<String, Object> auditCommon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.auditCommon(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/common/resAuditCommon.action")
	@ResponseBody
	public Map<String, Object> resAuditCommon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.resAuditCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拿到id
	 */
	@RequestMapping("/common/getCommonId.action")
	@ResponseBody
	public Map<String, Object> getId(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", commonService.getId(caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拿到id 通过序列名称
	 */
	@RequestMapping("/common/getSequenceId.action")
	@ResponseBody
	public Map<String, Object> getSequenceId(String caller, String seqname) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", commonService.getSequenceid(seqname));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 禁用
	 */
	@RequestMapping("/common/bannedCommon.action")
	@ResponseBody
	public Map<String, Object> bannedCurrencys(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.bannedCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反禁用
	 */
	@RequestMapping("/common/resBannedCommon.action")
	@ResponseBody
	public Map<String, Object> resBannedCurrencys(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.resBannedCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 結案
	 */
	@RequestMapping("/common/endCommon.action")
	@ResponseBody
	public Map<String, Object> endCurrencys(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.endCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反結案
	 */
	@RequestMapping("/common/resEndCommon.action")
	@ResponseBody
	public Map<String, Object> resEndCurrencys(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.resEndCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/common/postCommon.action")
	@ResponseBody
	public Map<String, Object> postCommon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.postCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确认,操作是确认从B2B下载来的PO，确认除了更新确认状态，还要更新相关字段，eg:录入人，客户/供应商编号等
	 */
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
	
	/**
	 * 更新(非在录入状态下更新主表信息)
	 */
	@RequestMapping("/oa/form/modify.action")
	@ResponseBody
	public Map<String, Object> modify(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.modify(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 非在录入状态更新明细数据
	 */
	@RequestMapping(value = "/oa/modifyDetail.action")
	@ResponseBody
	public Map<String, Object> modifyGrid(String caller,String param,String log) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.modifyDetail(caller, param,log);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 获得打印header及bottom
	 */
	@RequestMapping(value = "/common/getPrintSet.action")
	@ResponseBody
	public Map<String, Object> getPrintSet() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", commonService.getPrintSet());
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/common/turnCommon.action")
	@ResponseBody
	public Map<String, Object> turn(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", commonService.turnCommon(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/common/getButtonconfigs.action")
	@ResponseBody
	public Map<String, Object> getButtonconfigs(String caller) {
		return commonService.getButtonconfigs(caller);
	}
	@RequestMapping("oa/common/turnAllCommon.action")
	@ResponseBody
	public Map<String, Object> turnAllCommon(String caller,int id,String name) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", commonService.turnAllCommon(caller,id,name));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 *  maz 根据单据caller拿到queryConfigs信息
	 */
	@RequestMapping("/common/getqueryConfigs.action")
	@ResponseBody
	public Map<String, Object> getqueryConfigs(String caller ,String xtype) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", commonService.getqueryConfigs(caller,xtype));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取银行行名
	 */
	@RequestMapping("/common/getBankName.action")
	@ResponseBody
	public Map<String,Object> getBankName(String condition,Integer page,Integer start,Integer limit ){
		Integer end=start+limit+1;
		Map<String,Object> map=commonService.getBankName(condition,start,end);
		map.put("success",true);
		return map;
	}
	
	/**
	 *转无效
	 */
	@RequestMapping("/common/abate.action")
	@ResponseBody
	public Map<String,Object> abate(Integer id, String caller, String remark){
		Map<String, Object> map = new HashMap<String, Object>();
		commonService.abate(id, caller, remark);
		map.put("success",true);
		return map;
	}
	
	/**
	 * 转有效
	 */
	@RequestMapping("/common/resAbate.action")
	@ResponseBody
	public Map<String,Object> resAbate(Integer id, String caller, String remark){
		Map<String, Object> map = new HashMap<String, Object>();
		commonService.resAbate(id, caller, remark);
		map.put("success",true);
		return map;
	}
}
