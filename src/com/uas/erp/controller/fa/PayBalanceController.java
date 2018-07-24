package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.PayBalanceService;

@Controller
public class PayBalanceController {
	@Autowired
	private PayBalanceService payBalanceService;

	/**
	 * 清除已经抓取的预收账款
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/PayBalanceController/cleanAB.action")
	@ResponseBody
	public Map<String, Object> cleanAB(HttpSession session, String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.cleanAB(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 抓取预收账款
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/PayBalanceController/catchAB.action")
	@ResponseBody
	public Map<String, Object> catchAB(HttpSession session, String caller, String formStore, String param, String startdate, String enddate, String bicode) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.catchAB(caller, formStore, startdate, enddate, bicode);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayBalanceController/savePayBalance.action")
	@ResponseBody
	public Map<String, Object> savePayBalance(HttpSession session, String caller, String formStore, String param, String param2,
			String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.savePayBalance(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayBalanceController/deletePayBalance.action")
	@ResponseBody
	public Map<String, Object> deletePayBalance(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.deletePayBalance(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayBalanceController/updatePayBalance.action")
	@ResponseBody
	public Map<String, Object> updatePayBalance(HttpSession session, String caller, String formStore, String param, String param2,
			String param3) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.updatePayBalanceById(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayBalanceController/submitPayBalance.action")
	@ResponseBody
	public Map<String, Object> submitPayBalance(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.submitPayBalance(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayBalanceController/resSubmitPayBalance.action")
	@ResponseBody
	public Map<String, Object> resSubmitPayBalance(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.resSubmitPayBalance(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayBalanceController/auditPayBalance.action")
	@ResponseBody
	public Map<String, Object> auditPayBalance(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.auditPayBalance(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayBalanceController/resAuditPayBalance.action")
	@ResponseBody
	public Map<String, Object> resAuditPayBalance(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.resAuditPayBalance(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayBalanceController/postPayBalance.action")
	@ResponseBody
	public Map<String, Object> postPayBalance(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.postPayBalance(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayBalanceController/resPostPayBalance.action")
	@ResponseBody
	public Map<String, Object> resPostPayBalance(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.resPostPayBalance(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 预付冲应付的界面保存后台 此处会传送一个form 两个grid 的数据 分别为grid1(保存的为预付账款的信息) grid2(不影响逻辑)
	 * 
	 * grid1的值如果不够 form中的预付金额 则自动抓取预付账款到 paybalanceprdetail 表中
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/PayBalanceController/savePayBalancePRDetail.action")
	@ResponseBody
	public Map<String, Object> saveRecBalancePRDetail(HttpSession session, String caller, String formStore, String param, String param2,
			String param3) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.savePayBalancePRDetail(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayBalanceController/updatePayBalancePRDetailById.action")
	@ResponseBody
	public Map<String, Object> updateRecBalancePRDetailById(HttpSession session, String caller, String formStore, String param,
			String param2, String param3) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.updatePayBalancePRDetailById(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 抓取预付账款
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/PayBalanceController/catchPP.action")
	@ResponseBody
	public Map<String, Object> catchPP(HttpSession session, String caller, String formStore, String param, String startdate, String enddate) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.catchPP(caller, formStore, startdate, enddate);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 清除已经抓取的预付账款
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/PayBalanceController/cleanPP.action")
	@ResponseBody
	public Map<String, Object> cleanPP(HttpSession session, String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.cleanPP(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应付冲应收界面：清除已经抓取的应付发票
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/PayBalanceController/cleanAP.action")
	@ResponseBody
	public Map<String, Object> cleanAP(HttpSession session, String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.cleanAP(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收冲应付界面：抓取应付发票
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/PayBalanceController/catchAP.action")
	@ResponseBody
	public Map<String, Object> catchAP(HttpSession session, String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.catchAP(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收冲应付界面：清除已经抓取的应收发票
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/PayBalanceController/cleanAR.action")
	@ResponseBody
	public Map<String, Object> cleanAR(HttpSession session, String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.cleanAR(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收冲应付界面：抓取应收发票
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/payBalanceController/catchAR.action")
	@ResponseBody
	public Map<String, Object> catchAR(HttpSession session, String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.catchAR(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收冲应付界面：保存后台 此处会传送一个form 两个grid 的数据 分别为grid1(保存的为应付发票的信息)
	 * grid2(保存的为应收发票的信息，不影响逻辑) grid1的值如果不够 form中的预收金额 则自动抓取预收账款到
	 * recbalanceprdetail 表中
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/PayBalanceController/savePayBalanceAR.action")
	@ResponseBody
	public Map<String, Object> savePayBalanceAR(HttpSession session, String caller, String formStore, String param, String param2) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.savePayBalanceAR(caller, formStore, param, param2);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayBalanceController/updatePayBalanceARById.action")
	@ResponseBody
	public Map<String, Object> updatePayBalanceARById(HttpSession session, String caller, String formStore, String param, String param2) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payBalanceService.updatePayBalanceARById(caller, formStore, param, param2);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/PayBalanceController/printPayBalance.action")
	@ResponseBody
	public Map<String, Object> printPayBalance(HttpSession session, int id, String reportName, String condition, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = payBalanceService.printPayBalance(id, reportName, condition, caller);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	@RequestMapping("/fa/arp/getPrePay.action")
	@ResponseBody
	public Map<String, Object> getPrePay(String vendcode, String currency) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", payBalanceService.getPrePay(vendcode, currency));
		return modelMap;
	}

	@RequestMapping("/fa/arp/getAPBill.action")
	@ResponseBody
	public Map<String, Object> getAPBill(String vendcode, String currency) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", payBalanceService.getAPBill(vendcode, currency));
		return modelMap;
	}
}
