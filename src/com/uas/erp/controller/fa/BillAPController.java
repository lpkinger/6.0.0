package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.BillAPService;

@Controller
public class BillAPController extends BaseController {
	@Autowired
	private BillAPService billAPService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/gs/saveBillAP.action")
	@ResponseBody
	public Map<String, Object> saveBillAP(String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPService.saveBillAP(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/gs/deleteBillAP.action")
	@ResponseBody
	public Map<String, Object> deleteBillAP(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPService.deleteBillAP(id, caller);
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
	@RequestMapping("/fa/gs/updateBillAP.action")
	@ResponseBody
	public Map<String, Object> updateBillAP(String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPService.updateBillAPById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/gs/printBillAP.action")
	@ResponseBody
	public Map<String, Object> printBillAP(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPService.printBillAP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/gs/submitBillAP.action")
	@ResponseBody
	public Map<String, Object> submitBillAP(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPService.submitBillAP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/gs/resSubmitBillAP.action")
	@ResponseBody
	public Map<String, Object> resSubmitBillAP(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPService.resSubmitBillAP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/gs/auditBillAP.action")
	@ResponseBody
	public Map<String, Object> auditBillAP(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", billAPService.auditBillAP(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/gs/resAuditBillAP.action")
	@ResponseBody
	public Map<String, Object> resAuditBillAP(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPService.resAuditBillAP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 作废
	 */
	@RequestMapping("/fa/gs/nullifyBillAP.action")
	@ResponseBody
	public Map<String, Object> nullifyBillAP(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPService.nullifyBillAP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 寄出领取
	 */
	@RequestMapping(value = "/fa/gs/getSendDeal.action")
	@ResponseBody
	public Map<String, Object> getSendDeal(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPService.getSend(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新信息
	 * */
	@RequestMapping("/fa/gs/billap/updateInfo.action")
	@ResponseBody
	public Map<String, Object> updateInfo(int id, String text, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPService.updateInfo(id, text, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 复制
	 */
	@RequestMapping("/fa/gs/copyBillAP.action")
	@ResponseBody
	public Map<String, Object> copyBillAP(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ar", billAPService.copyBillAP(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
