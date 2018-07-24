package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.ARBillService;
import com.uas.webkit.sse.ResponseEmitter;

@Controller("arbillController")
public class ARBillController extends BaseController {
	@Autowired
	private ARBillService arBillService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/ars/saveARBill.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore, String param, String param2, String param3, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBillService.saveARBill(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/ars/deleteARBill.action")
	@ResponseBody
	public Map<String, Object> deleteARBill(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBillService.deleteARBill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/ars/updateARBill.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore, String param, String param2, String param3, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBillService.updateARBillById(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/ars/printARBill.action")
	@ResponseBody
	public Map<String, Object> printARBill(HttpSession session, int id, String caller, String reportName, String condition) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = arBillService.printARBill(caller, id, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/ars/printVoucherCodeARBill.action")
	@ResponseBody
	public Map<String, Object> printVoucherCodeARBill(HttpSession session, int id, String caller, String reportName, String condition) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = arBillService.printVoucherCodeARBill(caller, id, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * @param session
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/ars/beforeUpdateARBill.action")
	@ResponseBody
	public Map<String, Object> beforeUpdateARBill(HttpSession session, String params) {
		Map<String, Object> modelMap = new HashMap<String, Object>();

		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/ars/submitARBill.action")
	@ResponseBody
	public Map<String, Object> submitARBill(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBillService.submitARBill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/ars/resSubmitARBill.action")
	@ResponseBody
	public Map<String, Object> resSubmitARBill(HttpSession session, int id, String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBillService.resSubmitARBill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/ars/auditARBill.action")
	@ResponseBody
	public Map<String, Object> auditARBill(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBillService.auditARBill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/ars/resAuditARBill.action")
	@ResponseBody
	public Map<String, Object> resAuditARBill(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBillService.resAuditARBill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/fa/ars/postARBill.action")
	@ResponseBody
	public Map<String, Object> postProdInOut(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBillService.postARBill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/fa/ars/resPostARBill.action")
	@ResponseBody
	public Map<String, Object> resPostProdInOut(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBillService.resPostARBill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 凭证制作
	 */
	@RequestMapping("/fa/ars/createVoucherARO.action")
	@ResponseBody
	public Map<String, Object> createVoucher(HttpSession session, String abcode, String abdate, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBillService.createVoucherARO(abcode, abdate, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 
	 * @param session
	 * @param caller
	 *            应收发票批量过账
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/fa/ARBillController/vastARBillPost.action")
	@ResponseBody
	public Map<String, Object> vastARBillPost(HttpSession session, String caller, String data) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = arBillService.vastPostARBill(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确认现金收款
	 */
	@RequestMapping("/fa/ars/confirmXJSK.action")
	@ResponseBody
	public Map<String, Object> confirmXJSK(HttpSession session, int id, String catecode, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String logString = arBillService.confirmXJSK(id, catecode, caller);
		modelMap.put("success", true);
		modelMap.put("log", logString);
		return modelMap;
	}

	/**
	 * 确认现样机收款
	 */
	@RequestMapping("/fa/ars/confirmYJSK.action")
	@ResponseBody
	public Map<String, Object> confirmYJSK(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String logString = arBillService.confirmYJSK(id, caller);
		modelMap.put("success", true);
		modelMap.put("log", logString);
		return modelMap;
	}

	/**
	 * 取消收款
	 */
	@RequestMapping("/fa/ars/cancelSK.action")
	@ResponseBody
	public Map<String, Object> cancelXJSK(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String logString = arBillService.cancelXJSK(id, caller);
		modelMap.put("success", true);
		modelMap.put("log", logString);
		return modelMap;
	}

	/**
	 * 更新税票信息
	 */
	@RequestMapping("/fa/ars/updateARBillTaxcode.action")
	@ResponseBody
	public Map<String, Object> updateTaxcode(String caller, int ab_id, String ab_refno, String ab_remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBillService.updateTaxcode(caller, ab_id, ab_refno, ab_remark);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 复制
	 */
	@RequestMapping("/fa/ars/copyARBill.action")
	@ResponseBody
	public Map<String, Object> copyARBill(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ab", arBillService.copyARBill(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 复制
	 */
	@RequestMapping("/fa/ars/getOrderType.action")
	@ResponseBody
	public Map<String, Object> getOrderType(String caller, int id, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", arBillService.getOrderType(caller, id, code));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收发票明细辅助核算
	 * 
	 * @param ar_id
	 * @return
	 */
	@RequestMapping(value = "/fa/ars/getAPARDetailAss.action")
	@ResponseBody
	public ModelMap getAccountRegisterAss(int ab_id, String type) {
		return success(arBillService.findAss(ab_id, type));
	}

	/**
	 * 应收发票批量对账
	 */
	@RequestMapping(value = "/fa/vastCheckARBill.action")
	@ResponseBody
	@ResponseEmitter
	public Map<String, Object> vastCheckARBill(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = arBillService.vastCheckARBill(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收发票批量取消对账
	 */
	@RequestMapping(value = "/fa/vastResCheckARBill.action")
	@ResponseBody
	@ResponseEmitter
	public Map<String, Object> vastResCheckARBill(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = arBillService.vastResCheckARBill(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

}
