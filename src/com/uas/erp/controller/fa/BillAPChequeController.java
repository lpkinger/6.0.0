package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.BillAPChequeService;

@Controller
public class BillAPChequeController extends BaseController {
	@Autowired
	private BillAPChequeService billAPChequeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/gs/saveBillAPCheque.action")
	@ResponseBody
	public Map<String, Object> saveBillAPCheque(HttpSession session, String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChequeService.saveBillAPCheque(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/gs/deleteBillAPCheque.action")
	@ResponseBody
	public Map<String, Object> deleteBillAPCheque(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChequeService.deleteBillAPCheque(id, caller);
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
	@RequestMapping("/fa/gs/updateBillAPCheque.action")
	@ResponseBody
	public Map<String, Object> updateBillAPCheque(HttpSession session, String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChequeService.updateBillAPChequeById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/gs/printBillAPCheque.action")
	@ResponseBody
	public Map<String, Object> printBillAPCheque(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChequeService.printBillAPCheque(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/gs/submitBillAPCheque.action")
	@ResponseBody
	public Map<String, Object> submitBillAPCheque(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChequeService.submitBillAPCheque(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/gs/resSubmitBillAPCheque.action")
	@ResponseBody
	public Map<String, Object> resSubmitBillAPCheque(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChequeService.resSubmitBillAPCheque(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/gs/auditBillAPCheque.action")
	@ResponseBody
	public Map<String, Object> auditBillAPCheque(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChequeService.auditBillAPCheque(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/gs/resAuditBillAPCheque.action")
	@ResponseBody
	public Map<String, Object> resAuditBillAPCheque(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChequeService.resAuditBillAPCheque(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 结案
	 */
	@RequestMapping("/fa/gs/endBillAPCheque.action")
	@ResponseBody
	public Map<String, Object> endBillAPCheque(int id, String reason, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChequeService.endBillAPCheque(id, reason, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/fa/gs/resEndBillAPCheque.action")
	@ResponseBody
	public Map<String, Object> resEndBillAPCheque(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChequeService.resEndBillAPCheque(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 复制
	 */
	@RequestMapping("/fa/gs/copyBillAPCheque.action")
	@ResponseBody
	public Map<String, Object> copyBillAPCheque(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ar", billAPChequeService.copyBillAPCheque(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新信息
	 * */
	@RequestMapping("/fa/gs/BillAPCheque/updateInfo.action")
	@ResponseBody
	public Map<String, Object> updateInfo(int id, String text, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChequeService.updateInfo(id, text, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转银行登记
	 * */
	@RequestMapping("/fa/gs/apchequeToAccountRegister.action")
	@ResponseBody
	public Map<String, Object> chequeToAccountRegister(int id, String accountcode, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", billAPChequeService.turnAccountRegister(id, accountcode, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
