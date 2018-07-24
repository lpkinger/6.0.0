package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.RecBalanceNoticeService;

@Controller
public class RecBalanceNoticeController extends BaseController {
	@Autowired
	private RecBalanceNoticeService recBalanceNoticeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/ars/saveRecBalanceNotice.action")
	@ResponseBody
	public Map<String, Object> saveRecBalanceNotice(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceNoticeService.saveRecBalanceNotice(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 */
	@RequestMapping("/fa/ars/deleteRecBalanceNotice.action")
	@ResponseBody
	public Map<String, Object> deleteRecBalanceNotice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceNoticeService.deleteRecBalanceNotice(id, caller);
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
	@RequestMapping("/fa/ars/updateRecBalanceNotice.action")
	@ResponseBody
	public Map<String, Object> updateRecBalanceNotice(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceNoticeService.updateRecBalanceNoticeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/ars/submitRecBalanceNotice.action")
	@ResponseBody
	public Map<String, Object> submitRecBalanceNotice(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceNoticeService.submitRecBalanceNotice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/ars/resSubmitRecBalanceNotice.action")
	@ResponseBody
	public Map<String, Object> resSubmitRecBalanceNotice(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceNoticeService.resSubmitRecBalanceNotice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/ars/auditRecBalanceNotice.action")
	@ResponseBody
	public Map<String, Object> auditRecBalanceNotice(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceNoticeService.auditRecBalanceNotice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/ars/resAuditRecBalanceNotice.action")
	@ResponseBody
	public Map<String, Object> resAuditRecBalanceNotice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceNoticeService.resAuditRecBalanceNotice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转银行登记
	 */
	@RequestMapping("/fa/ars/turnAccountRegister.action")
	@ResponseBody
	public Map<String, Object> turnAccountRegister(int id, String catecode, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", recBalanceNoticeService.turnAccountRegister(id, catecode, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转应收票据
	 */
	@RequestMapping("/fa/ars/turnBillAR.action")
	@ResponseBody
	public Map<String, Object> turnBillAR(int id, String catecode, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", recBalanceNoticeService.turnBillAR(id, catecode, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 回款通知单：清除已经抓取的应收发票
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/ars/cleanRecBalanceNoticeAB.action")
	@ResponseBody
	public Map<String, Object> cleanAB(String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceNoticeService.cleanAB(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 回款通知单：抓取应收发票
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/ars/catchRecBalanceNoticeAB.action")
	@ResponseBody
	public Map<String, Object> catchAB(String caller, String formStore, String startdate, String enddate, String bicode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceNoticeService.catchAB(caller, formStore, startdate, enddate, bicode);
		modelMap.put("success", true);
		return modelMap;
	}

}
