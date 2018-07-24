package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.BillARService;

@Controller
public class BillARController extends BaseController {
	@Autowired
	private BillARService billARService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/gs/saveBillAR.action")
	@ResponseBody
	public Map<String, Object> saveBillAR(HttpSession session, String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARService.saveBillAR(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/gs/deleteBillAR.action")
	@ResponseBody
	public Map<String, Object> deleteBillAR(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARService.deleteBillAR(id, caller);
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
	@RequestMapping("/fa/gs/updateBillAR.action")
	@ResponseBody
	public Map<String, Object> updateBillAR(HttpSession session, String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARService.updateBillARById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/gs/printBillAR.action")
	@ResponseBody
	public Map<String, Object> printBillAR(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARService.printBillAR(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/gs/submitBillAR.action")
	@ResponseBody
	public Map<String, Object> submitBillAR(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARService.submitBillAR(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/gs/resSubmitBillAR.action")
	@ResponseBody
	public Map<String, Object> resSubmitBillAR(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARService.resSubmitBillAR(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/gs/auditBillAR.action")
	@ResponseBody
	public Map<String, Object> auditBillAR(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARService.auditBillAR(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/gs/resAuditBillAR.action")
	@ResponseBody
	public Map<String, Object> resAuditBillAR(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARService.resAuditBillAR(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 作废
	 */
	@RequestMapping("/fa/gs/nullifyBillAR.action")
	@ResponseBody
	public Map<String, Object> nullifyBillAP(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARService.nullifyBillAR(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 托收
	 */
	@RequestMapping(value = "/fa/gs/vastChangeBank.action")
	@ResponseBody
	public Map<String, Object> vastEndPurchase(HttpSession session, String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARService.changeBank(caller, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 复制
	 */
	@RequestMapping("/fa/gs/copyBillAR.action")
	@ResponseBody
	public Map<String, Object> copyBillAR(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ar", billARService.copyBillAR(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新信息
	 * */
	@RequestMapping("/fa/gs/billar/updateInfo.action")
	@ResponseBody
	public Map<String, Object> updateInfo(HttpSession session, int id, String text, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARService.updateInfo(id, text, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/gs/saveBillARSplit.action")
	@ResponseBody
	public Map<String, Object> saveBillARSplit(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARService.updateBillARSplit(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拆分应收票据明细
	 */
	@RequestMapping("/fa/gs/splitDetailBillAR.action")
	@ResponseBody
	public Map<String, Object> splitDetailBillAR(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARService.splitDetailBillAR(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取消拆分应收票据明细
	 */
	@RequestMapping("/fa/gs/cancelSplitDetailBillAR.action")
	@ResponseBody
	public Map<String, Object> cancelSplitDetailBillAR(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARService.cancelSplitDetailBillAR(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拆分应收票据明细所有未拆分的数据
	 */
	@RequestMapping("/fa/gs/splitBillAR.action")
	@ResponseBody
	public Map<String, Object> splitBillAR(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARService.splitBillAR(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
