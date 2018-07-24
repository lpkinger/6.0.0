package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.BillARChequeService;

@Controller
public class BillARChequeController extends BaseController {
	@Autowired
	private BillARChequeService billARChequeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/gs/saveBillARCheque.action")
	@ResponseBody
	public Map<String, Object> saveBillARCheque(HttpSession session, String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChequeService.saveBillARCheque(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/gs/deleteBillARCheque.action")
	@ResponseBody
	public Map<String, Object> deleteBillARCheque(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChequeService.deleteBillARCheque(id, caller);
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
	@RequestMapping("/fa/gs/updateBillARCheque.action")
	@ResponseBody
	public Map<String, Object> updateBillARCheque(HttpSession session, String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChequeService.updateBillARChequeById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/gs/printBillARCheque.action")
	@ResponseBody
	public Map<String, Object> printBillARCheque(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChequeService.printBillARCheque(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/gs/submitBillARCheque.action")
	@ResponseBody
	public Map<String, Object> submitBillARCheque(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChequeService.submitBillARCheque(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/gs/resSubmitBillARCheque.action")
	@ResponseBody
	public Map<String, Object> resSubmitBillARCheque(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChequeService.resSubmitBillARCheque(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/gs/auditBillARCheque.action")
	@ResponseBody
	public Map<String, Object> auditBillARCheque(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChequeService.auditBillARCheque(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/gs/resAuditBillARCheque.action")
	@ResponseBody
	public Map<String, Object> resAuditBillARCheque(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChequeService.resAuditBillARCheque(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 结案
	 */
	@RequestMapping("/fa/gs/endBillARCheque.action")
	@ResponseBody
	public Map<String, Object> endBillARCheque(int id, String reason, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChequeService.endBillARCheque(id, reason, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/fa/gs/resEndBillARCheque.action")
	@ResponseBody
	public Map<String, Object> resEndBillARCheque(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChequeService.resEndBillARCheque(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 复制
	 */
	@RequestMapping("/fa/gs/copyBillARCheque.action")
	@ResponseBody
	public Map<String, Object> copyBillARCheque(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ar", billARChequeService.copyBillARCheque(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新信息
	 * */
	@RequestMapping("/fa/gs/billARCheque/updateInfo.action")
	@ResponseBody
	public Map<String, Object> updateInfo(int id, String text, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChequeService.updateInfo(id, text, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转银行登记
	 * */
	@RequestMapping("/fa/gs/chequeToAccountRegister.action")
	@ResponseBody
	public Map<String, Object> chequeToAccountRegister(int id, String accountcode, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", billARChequeService.turnAccountRegister(id, accountcode, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
