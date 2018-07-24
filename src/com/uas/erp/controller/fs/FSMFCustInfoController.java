package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.FSMFCustInfoService;

@Controller
public class FSMFCustInfoController {

	@Autowired
	private FSMFCustInfoService fsMFCustInfoService;

	/**
	 * 保存
	 */
	@RequestMapping("/fs/cust/saveFSMFCustInfo.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsMFCustInfoService.saveFSMFCustInfo(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fs/cust/updateFSMFCustInfo.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsMFCustInfoService.updateFSMFCustInfo(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/cust/deleteFSMFCustInfo.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsMFCustInfoService.deleteFSMFCustInfo(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/cust/submitFSMFCustInfo.action")
	@ResponseBody
	public Map<String, Object> submitFSMFCustInfo(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsMFCustInfoService.submitFSMFCustInfo(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/cust/resSubmitFSMFCustInfo.action")
	@ResponseBody
	public Map<String, Object> resSubmitFSMFCustInfo(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsMFCustInfoService.resSubmitFSMFCustInfo(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/cust/auditFSMFCustInfo.action")
	@ResponseBody
	public Map<String, Object> auditFSMFCustInfo(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsMFCustInfoService.auditFSMFCustInfo(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/cust/resAuditFSMFCustInfo.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsMFCustInfoService.resAuditFSMFCustInfo(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存
	 */
	@RequestMapping("/fs/cust/saveFSMFCustInfoDet.action")
	@ResponseBody
	public Map<String, Object> saveFSMFCustInfoDet(String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsMFCustInfoService.saveFSMFCustInfoDet(gridStore);
		modelMap.put("success", true);
		return modelMap;
	}

}
