package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.APBillService;

@Controller("apbillController")
public class APBillController extends BaseController {
	@Autowired
	private APBillService apBillService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/ars/saveAPBill.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String param2, String param3, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		apBillService.saveAPBill(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/ars/deleteAPBill.action")
	@ResponseBody
	public Map<String, Object> deleteAPBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		apBillService.deleteAPBill(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/ars/updateAPBill.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String param2, String param3, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		apBillService
				.updateAPBillById(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/ars/printAPBill.action")
	@ResponseBody
	public Map<String, Object> printAPBill(HttpSession session, int id,
			String caller, String reportName, String condition) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = apBillService.printAPBill(caller, id, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
	
	/**
	 * 打印
	 */
	@RequestMapping("/fa/ars/printVoucherCodeAPBill.action")
	@ResponseBody
	public Map<String, Object> printVoucherCodeAPBill(HttpSession session, int id,
			String caller, String reportName, String condition) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = apBillService.printVoucherCodeAPBill(caller, id, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * @param session
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/ars/beforeUpdateAPBill.action")
	@ResponseBody
	public Map<String, Object> beforeUpdateAPBill(HttpSession session,
			String params) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/ars/submitAPBill.action")
	@ResponseBody
	public Map<String, Object> submitAPBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		apBillService.submitAPBill(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/ars/resSubmitAPBill.action")
	@ResponseBody
	public Map<String, Object> resSubmitAPBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		apBillService.resSubmitAPBill(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/ars/auditAPBill.action")
	@ResponseBody
	public Map<String, Object> auditAPBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		apBillService.auditAPBill(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/ars/resAuditAPBill.action")
	@ResponseBody
	public Map<String, Object> resAuditAPBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		apBillService.resAuditAPBill(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/fa/ars/postAPBill.action")
	@ResponseBody
	public Map<String, Object> postProdInOut(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		apBillService.postAPBill(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/fa/ars/resPostAPBill.action")
	@ResponseBody
	public Map<String, Object> resPostProdInOut(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		apBillService.resPostAPBill(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 凭证制作
	 */
	@RequestMapping("/fa/arp/createVoucherAPO.action")
	@ResponseBody
	public Map<String, Object> createVoucher(HttpSession session,
			String abcode, String abdate) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		apBillService.createVoucherAPO(abcode, abdate);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 
	 * @param session
	 * @param caller
	 *            应付发票批量过账
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/fa/APBillController/vastAPBillPost.action")
	@ResponseBody
	public Map<String, Object> vastARBillPost(HttpSession session,
			String caller, String data) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = apBillService.vastPostAPBill(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改开票日期
	 * 
	 * @param value
	 *            新开票日期
	 */
	@RequestMapping("/fa/arp/updateBillDate.action")
	@ResponseBody
	public Map<String, Object> updateBillDate(HttpSession session, Integer id,
			String date, String yearmonth) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		apBillService.updateBillDate(id, date, yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 确认对账
	 */
	@RequestMapping("/fa/arp/confirmAPBill.action")
	@ResponseBody
	public Map<String, Object> confirmCheck(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apBillService.confirmCheck(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取消对账
	 */
	@RequestMapping("/fa/arp/cancelAPBill.action")
	@ResponseBody
	public Map<String, Object> cancelCheck(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apBillService.cancelCheck(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新税票信息
	 */
	@RequestMapping("/fa/arp/updateAPBillTaxcode.action")  
	@ResponseBody 
	public Map<String, Object> updateTaxcode(String caller, int ab_id, String ab_refno, String ab_remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apBillService.updateTaxcode(caller, ab_id, ab_refno, ab_remark);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 复制
	 */
	@RequestMapping("/fa/arp/copyAPBill.action")
	@ResponseBody
	public Map<String, Object> copyARBill(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ab", apBillService.copyAPBill(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

}
