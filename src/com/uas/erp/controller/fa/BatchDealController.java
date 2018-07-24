package com.uas.erp.controller.fa;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.service.fa.BatchDealService;
import com.uas.erp.service.ma.ConfigService;
import com.uas.webkit.sse.ResponseEmitter;

/**
 * Fa模块 批量处理
 */
@Controller("FaBatchDealController")
public class BatchDealController {
	@Autowired
	private BatchDealService batchDealService;
	@Autowired
	private ConfigService configService;

	/**
	 * 按应收开票模式，切换到不同开票界面
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/fa/view/turnARBill.action")
	public void redirectARBill(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject config = configService.getConfigByCallerAndCode("sys", "autoCreateArBill");
		JSONObject config2 = configService.getConfigByCallerAndCode("sys", "useBillOutAR");
		String basePath = BaseUtil.getBasePath(request);
		if (String.valueOf(Constant.YES).equals(config.get("data")) && String.valueOf(Constant.YES).equals(config2.get("data"))) {
			response.sendRedirect(basePath + "jsps/common/batchDeal.jsp?whoami=ARBill!ToBillOut!Deal&_noc=1");
		} else
			response.sendRedirect(basePath + "jsps/fa/ars/batchDeal.jsp?whoami=ProdInOut!ToARBill!Deal!ars&_noc=1");
	}

	/**
	 * 按应付开票模式，切换到不同开票界面
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/fa/view/turnAPBill.action")
	public void redirectAPBill(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject config = configService.getConfigByCallerAndCode("sys", "autoCreateApBill");
		JSONObject config2 = configService.getConfigByCallerAndCode("sys", "useBillOutAP");
		String basePath = BaseUtil.getBasePath(request);
		if (String.valueOf(Constant.YES).equals(config.get("data")) && String.valueOf(Constant.YES).equals(config2.get("data"))) {
			response.sendRedirect(basePath + "jsps/common/batchDeal.jsp?whoami=APBill!ToBillOutAP!Deal&_noc=1");
		} else
			response.sendRedirect(basePath + "jsps/fa/ars/batchDeal.jsp?whoami=ProdInOut!ToAPBill!Deal!ars&_noc=1");
	}

	/**
	 * 按应付开票模式，切换到不同对账界面
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/fa/view/turnAPCheck.action")
	public void redirectAPCheck(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject config = configService.getConfigByCallerAndCode("sys", "autoCreateApBill");
		String basePath = BaseUtil.getBasePath(request);
		if (String.valueOf(Constant.YES).equals(config.get("data"))) {
			response.sendRedirect(basePath + "jsps/fa/ars/batchDeal.jsp?whoami=APBill!ToAPCheck!Deal&_noc=1");
		} else
			response.sendRedirect(basePath + "jsps/fa/ars/batchDeal.jsp?whoami=ProdInOut!ToAPCheck!Deal&_noc=1");
	}

	/**
	 * 按应收开票模式，切换到不同对账界面
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/fa/view/turnARCheck.action")
	public void redirectARCheck(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject config = configService.getConfigByCallerAndCode("sys", "autoCreateArBill");
		String basePath = BaseUtil.getBasePath(request);
		if (String.valueOf(Constant.YES).equals(config.get("data"))) {
			response.sendRedirect(basePath + "jsps/fa/ars/batchDeal.jsp?whoami=ARBill!ToARCheck!Deal&_noc=1");
		} else
			response.sendRedirect(basePath + "jsps/fa/ars/batchDeal.jsp?whoami=ProdInOut!ToARCheck!Deal&_noc=1");
	}

	/**
	 * 总分类账界面，切换到不同查询界面 单行Single，多行Multi 单行：按科目单行多列显示出期初借贷、本期借贷、本年累计借贷、期末借贷
	 * 多行：按科目分多行（期初、本期发生、本年累计、期末）显示借贷
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/fa/view/generalLedger.action")
	public void generalLedger(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject config = configService.getConfigByCallerAndCode("sys", "GLStyle");
		String basePath = BaseUtil.getBasePath(request);
		if (String.valueOf("Multi").equals(config.get("data"))) {
			response.sendRedirect(basePath + "jsps/fa/gla/generalLedger.jsp?_noc=1");
		} else
			response.sendRedirect(basePath + "jsps/fa/gla/generalLedgerSingle.jsp?_noc=1");
	}

	/**
	 * 出货单整批开票
	 */
	@RequestMapping(value = "/fa/vastTurnARBill.action")
	@ResponseBody
	@ResponseEmitter
	public Map<String, Object> vastTurnARBill(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnARBill(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 
	 * @param session
	 * @param caller
	 *            采购验收单批量开票
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/fa/vastTurnAPBill.action")
	@ResponseBody
	@ResponseEmitter
	public Map<String, Object> vastTurnAPBill(HttpSession session, String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnAPBill(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 
	 * @param session
	 * @param caller
	 *            付款申请单转支票作业
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/fa/vastTurnBillAP.action")
	@ResponseBody
	public Map<String, Object> vastTurnBillAP(HttpSession session, String caller, String data) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnBillAP(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量转预收冲应收
	 */
	@RequestMapping(value = "/fa/vastTurnRecBalance.action")
	@ResponseBody
	public Map<String, Object> vastTurnRecBalance(HttpSession session, String caller, String data) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnRecBalance(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量转预付冲应付
	 */
	@RequestMapping(value = "/fa/vastTurnPayBalance.action")
	@ResponseBody
	public Map<String, Object> vastTurnPayBalance(HttpSession session, String caller, String data) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnPayBalance(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 银行存款总账查询 更新
	 */
	@RequestMapping(value = "/fa/vastALMonthUpdate.action")
	@ResponseBody
	public Map<String, Object> vastALMonthUpdate(HttpSession session, String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastALMonthUpdate(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 付款申请批量转付款单或预付单
	 */
	@RequestMapping(value = "/fa/vastToPBorPP.action")
	@ResponseBody
	public Map<String, Object> vastToPBorPP(HttpSession session, String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastToPBorPP(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收发票整批开票
	 */
	@RequestMapping(value = "/fa/vastTurnBillOut.action")
	@ResponseBody
	@ResponseEmitter
	public Map<String, Object> vastTurnBillOut(HttpSession session, String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnBillOut(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应付发票整批开票
	 */
	@RequestMapping(value = "/fa/vastTurnBillOutAP.action")
	@ResponseBody
	@ResponseEmitter
	public Map<String, Object> vastTurnBillOutAP(HttpSession session, String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnBillOutAP(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 银行登记批量提交
	 */
	@RequestMapping(value = "/fa/vastSubmitAccountRegister.action")
	@ResponseBody
	public Map<String, Object> vastSubmitAccountRegister(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastSubmitAccountRegister(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 银行登记批量对账作业
	 */
	@RequestMapping(value = "/fa/vastConfirmCheckRegister.action")
	@ResponseBody
	public Map<String, Object> vastConfirmCheckRegister(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastConfirmCheckRegister(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 银行登记批量取消对账作业
	 */
	@RequestMapping(value = "/fa/vastCancelCheckRegister.action")
	@ResponseBody
	public Map<String, Object> vastCancelCheckRegister(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastCancelCheckRegister(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量过账作业
	 */
	@RequestMapping(value = "/fa/faPost.action")
	@ResponseBody
	public Map<String, Object> faPost(String caller, String from, String to, String pclass) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.faPost(caller, from, to, pclass);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收批量对账作业
	 */
	@RequestMapping(value = "/fa/vastTurnARCheck.action")
	@ResponseBody
	public Map<String, Object> vastTurnARCheck(String caller, String data, String fromDate, String toDate) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnARCheck(caller, data, fromDate, toDate);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应付批量对账作业
	 */
	@RequestMapping(value = "/fa/vastTurnAPCheck.action")
	@ResponseBody
	public Map<String, Object> vastTurnAPCheck(String caller, String data, String fromDate, String toDate) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnAPCheck(caller, data, fromDate, toDate);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 预付-应付明细确认
	 */
	@RequestMapping(value = "/fa/confirmPrePayAPBill.action")
	@ResponseBody
	public Map<String, Object> confirmPrePayAPBill(int vmid, double thisamount, String data1, String data2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.confirmPrePayAPBill(vmid, thisamount, data1, data2);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 预收-应收明细确认
	 */
	@RequestMapping(value = "/fa/confirmPreRecARBill.action")
	@ResponseBody
	public Map<String, Object> confirmPreRecARBill(int cmid, double thisamount, String data1, String data2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.confirmPreRecARBill(cmid, thisamount, data1, data2);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收对账单批量确认
	 */
	@RequestMapping(value = "/fa/ars/vastARCheckConfirm.action")
	@ResponseBody
	public Map<String, Object> vastARCheckConfirm(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastARCheckConfirm(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 逾期应收单批量催收
	 */
	@RequestMapping(value = "/fa/fp/anticipateCollection.action")
	@ResponseBody
	public Map<String, Object> anticipateCollection(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.anticipateCollection(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}
}
