package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.BillOutAPService;

@Controller
public class BillOutAPController extends BaseController {
	@Autowired
	private BillOutAPService billOutAPService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/arp/saveBillOutAP.action")
	@ResponseBody
	public Map<String, Object> saveBillOutAP(HttpSession session, String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutAPService.saveBillOutAP(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/arp/deleteBillOutAP.action")
	@ResponseBody
	public Map<String, Object> deleteBillOutAP(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutAPService.deleteBillOutAP(id, caller);
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
	@RequestMapping("/fa/arp/updateBillOutAP.action")
	@ResponseBody
	public Map<String, Object> updateBillOutAP(HttpSession session, String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutAPService.updateBillOutAPById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/arp/printBillOutAP.action")
	@ResponseBody
	public Map<String, Object> printBillOutAP(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutAPService.printBillOutAP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/arp/submitBillOutAP.action")
	@ResponseBody
	public Map<String, Object> submitBillOutAP(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutAPService.submitBillOutAP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/arp/resSubmitBillOutAP.action")
	@ResponseBody
	public Map<String, Object> resSubmitBillOutAP(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutAPService.resSubmitBillOutAP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/arp/auditBillOutAP.action")
	@ResponseBody
	public Map<String, Object> auditBillOutAP(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutAPService.auditBillOutAP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/arp/resAuditBillOutAP.action")
	@ResponseBody
	public Map<String, Object> resAuditBillOutAP(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutAPService.resAuditBillOutAP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 记账
	 */
	@RequestMapping("/fa/arp/postBillOutAP.action")
	@ResponseBody
	public Map<String, Object> accountBillOutAP(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutAPService.accountedBillOutAP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反记账
	 */
	@RequestMapping("/fa/arp/resPostBillOutAP.action")
	@ResponseBody
	public Map<String, Object> resAccountBillOutAP(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutAPService.resAccountedBillOutAP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/ars/printBillOutAP.action")
	@ResponseBody
	public Map<String, Object> printBillOutAP(HttpSession session, int id, String reportName, String condition, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = billOutAPService.printBillOutAP(id, caller, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 按凭证号打印
	 */
	@RequestMapping("/fa/ars/printVoucherCodeBillOutAP.action")
	@ResponseBody
	public Map<String, Object> printVoucherCodeBillOutAP(HttpSession session, int id, String reportName, String condition, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = billOutAPService.printVoucherCodeBillOutAP(id, caller, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 更新税票信息
	 */
	@RequestMapping("/fa/arp/updateBillOutTaxcode.action")
	@ResponseBody
	public Map<String, Object> updateTaxcode(String caller, int bi_id, String bi_refno, String bi_remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutAPService.updateTaxcode(caller, bi_id, bi_refno, bi_remark);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转付款申请单
	 */
	@RequestMapping("/fa/arp/billoutToPayPlease.action")
	@ResponseBody
	public Map<String, Object> turnPayPlease(int id, String caller) {
		return success(billOutAPService.turnPayPlease(id, caller));
	}
}
