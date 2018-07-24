package com.uas.opensys.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.CommonService;


@Controller
public class OpenSysCommonController {
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
	@RequestMapping("/opensys/saveCommon.action")
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
	@RequestMapping("/opensys/deleteCommon.action")
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
	@RequestMapping("/opensys/updateCommon.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.updateCommonById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 提交
	 */
	@RequestMapping("/opensys/submitCommon.action")
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
	@RequestMapping("/opensys/resSubmitCommon.action")
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
	@RequestMapping("/opensys/auditCommon.action")
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
	@RequestMapping("/opensys/resAuditCommon.action")
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
	@RequestMapping("/opensys/getCommonId.action")
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
	@RequestMapping("/opensys/getSequenceId.action")
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
	@RequestMapping("/opensys/bannedCommon.action")
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
	@RequestMapping("/opensys/resBannedCommon.action")
	@ResponseBody
	public Map<String, Object> resBannedCurrencys(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.resBannedCommon(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 确认
	 */
	@RequestMapping("/opensys/openSysConfirmCommon.action")
	@ResponseBody
	public Map<String, Object> openSysConfirmCommon(String caller,int id,String confirmres,String confirmdesc) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonService.openSysConfirmCommon(caller, id,confirmres,confirmdesc);
		modelMap.put("success", true);
		return modelMap;
	}
}
