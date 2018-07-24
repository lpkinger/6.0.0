package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.OtherExplistService;

@Controller
public class OtherExplistController extends BaseController {
	@Autowired
	private OtherExplistService otherExplistService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/saveOtherExplist.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		otherExplistService.saveOtherExplist(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/mes/deleteOtherExplist.action")
	@ResponseBody
	public Map<String, Object> deleteOtherExplist(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		otherExplistService.deleteOtherExplist(id, caller);
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
	@RequestMapping("/pm/mes/updateOtherExplist.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		otherExplistService.updateOtherExplistById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/pm/mes/printOtherExplist.action")
	@ResponseBody
	public Map<String, Object> printOtherExplist(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		otherExplistService.printOtherExplist(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitOtherExplist.action")
	@ResponseBody
	public Map<String, Object> submitOtherExplist(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		otherExplistService.submitOtherExplist(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitOtherExplist.action")
	@ResponseBody
	public Map<String, Object> resSubmitOtherExplist(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		otherExplistService.resSubmitOtherExplist(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditOtherExplist.action")
	@ResponseBody
	public Map<String, Object> auditOtherExplist(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		otherExplistService.auditOtherExplist(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditOtherExplist.action")
	@ResponseBody
	public Map<String, Object> resAuditOtherExplist(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		otherExplistService.resAuditOtherExplist(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 结案
	 */
	@RequestMapping("/pm/mes/endOtherExplist.action")
	@ResponseBody
	public Map<String, Object> endOtherExplist(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		otherExplistService.endOtherExplist(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/pm/mes/resEndOtherExplist.action")
	@ResponseBody
	public Map<String, Object> resEndOtherExplist(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		otherExplistService.resEndOtherExplist(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 加工内容明细维护
	 */
	@RequestMapping("/pm/mes/updateOtherExplistDetail.action")
	@ResponseBody
	public Map<String, Object> updateOtherExplistDetail(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		otherExplistService.updateOtherExplistDetail(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新委外商、价格信息
	 * @param id 
	 * 			主表id
	 * @param vecode 
	 * 			委外商编号
	 * @param currency
	 * 			币别
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/updateOtherExplistInfo.action")
	@ResponseBody
	public Map<String, Object> updateOtherExplistInfo(String caller, int id,String vecode ,String currency, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		otherExplistService.updateOtherExplistInfo(id, vecode,currency ,param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
