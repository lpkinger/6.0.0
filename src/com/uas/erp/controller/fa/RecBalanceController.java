package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.RecBalanceService;

@Controller
public class RecBalanceController {
	@Autowired
	private RecBalanceService recBalanceService;

	/**
	 * 预收冲应收界面：清除已经抓取的预收账款
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/RecBalanceController/cleanPR.action")
	@ResponseBody
	public Map<String, Object> cleanPR(HttpSession session, String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.cleanPR(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 预收冲应收界面：抓取预收账款
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/RecBalanceController/catchPR.action")
	@ResponseBody
	public Map<String, Object> catchPR(HttpSession session, String caller, String formStore, String startdate, String enddate) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.catchPR(caller, formStore, startdate, enddate);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 预收冲应收界面：清除已经抓取的应收发票
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/RecBalanceController/cleanAB.action")
	@ResponseBody
	public Map<String, Object> cleanAB(HttpSession session, String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.cleanAB(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 预收冲应收界面：抓取应收发票
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/RecBalanceController/catchAB.action")
	@ResponseBody
	public Map<String, Object> catchAB(HttpSession session, String caller, String formStore, String startdate, String enddate, String bicode) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.catchAB(caller, formStore, startdate, enddate, bicode);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 预收冲应收的界面保存后台 此处会传送一个form 两个grid 的数据 分别为grid1(保存的为预收账款的信息) grid2(不影响逻辑)
	 * 
	 * grid1的值如果不够 form中的预收金额 则自动抓取预收账款到 recbalanceprdetail 表中
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/RecBalanceController/saveRecBalancePRDetail.action")
	@ResponseBody
	public Map<String, Object> saveRecBalancePRDetail(HttpSession session, String caller, String formStore, String param, String param2,
			String param3) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.saveRecBalancePRDetail(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/RecBalanceController/updateRecBalancePRDetailById.action")
	@ResponseBody
	public Map<String, Object> updateRecBalancePRDetailById(HttpSession session, String caller, String formStore, String param,
			String param2, String param3) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.updateRecBalancePRDetailById(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/saveRecBalance.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String caller, String formStore, String param, String param2, String param3) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.saveRecBalance(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/deleteRecBalance.action")
	@ResponseBody
	public Map<String, Object> deleteRecBalance(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.deleteRecBalance(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/updateRecBalance.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String caller, String formStore, String param, String param2, String param3) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.updateRecBalanceById(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/submitRecBalance.action")
	@ResponseBody
	public Map<String, Object> submitRecBalance(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.submitRecBalance(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/resSubmitRecBalance.action")
	@ResponseBody
	public Map<String, Object> resSubmitARBill(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.resSubmitRecBalance(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/auditRecBalance.action")
	@ResponseBody
	public Map<String, Object> auditARBill(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.auditRecBalance(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/resAuditRecBalance.action")
	@ResponseBody
	public Map<String, Object> resAuditARBill(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.resAuditRecBalance(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/postRecBalance.action")
	@ResponseBody
	public Map<String, Object> postRecBalance(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.postRecBalance(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/resPostRecBalance.action")
	@ResponseBody
	public Map<String, Object> resPostRecBalance(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.resPostRecBalance(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收冲应付界面：清除已经抓取的应付发票
	 * 
	 * @param session
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/RecBalanceController/cleanAP.action")
	@ResponseBody
	public Map<String, Object> cleanAP(HttpSession session, String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.cleanAP(caller, formStore);
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
	@RequestMapping("/fa/RecBalanceController/catchAP.action")
	@ResponseBody
	public Map<String, Object> catchAP(HttpSession session, String caller, String formStore, String startdate, String enddate) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.catchAP(caller, formStore, startdate, enddate);
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
	@RequestMapping("/fa/RecBalanceController/cleanAR.action")
	@ResponseBody
	public Map<String, Object> cleanAR(HttpSession session, String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.cleanAR(caller, formStore);
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
	@RequestMapping("/fa/RecBalanceController/catchAR.action")
	@ResponseBody
	public Map<String, Object> catchAR(HttpSession session, String caller, String formStore, String startdate, String enddate) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.catchAR(caller, formStore, startdate, enddate);
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
	@RequestMapping("/fa/RecBalanceController/saveRecBalanceAP.action")
	@ResponseBody
	public Map<String, Object> saveRecBalanceAP(HttpSession session, String caller, String formStore, String param, String param2,
			String param3, String param4) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.saveRecBalanceAP(caller, formStore, param, param2, param3, param4);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/RecBalanceController/updateRecBalanceAPById.action")
	@ResponseBody
	public Map<String, Object> updateRecBalanceAPById(HttpSession session, String caller, String formStore, String param, String param2,
			String param3, String param4) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceService.updateRecBalanceAPById(caller, formStore, param, param2, param3, param4);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/getPreRec.action")
	@ResponseBody
	public Map<String, Object> getPreRec(String custcode, String currency) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", recBalanceService.getPreRec(custcode, currency));
		return modelMap;
	}

	@RequestMapping("/fa/ars/getARBill.action")
	@ResponseBody
	public Map<String, Object> getARBill(String custcode, String currency) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", recBalanceService.getARBill(custcode, currency));
		return modelMap;
	}
}
