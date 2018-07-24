package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.BillOutService;

@Controller
public class BillOutController extends BaseController {
	@Autowired
	private BillOutService billOutService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/ars/saveBillOut.action")
	@ResponseBody
	public Map<String, Object> saveBillOut(HttpSession session,
			String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutService.saveBillOut(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/ars/deleteBillOut.action")
	@ResponseBody
	public Map<String, Object> deleteBillOut(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutService.deleteBillOut(id, caller);
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
	@RequestMapping("/fa/ars/updateBillOut.action")
	@ResponseBody
	public Map<String, Object> updateBillOut(HttpSession session,
			String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutService.updateBillOutById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/ars/printBillOut.action")
	@ResponseBody
	public Map<String, Object> printBillOut(HttpSession session, int id,
			String reportName, String condition, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = billOutService.printBillOut(id, caller, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
	
	/**
	 * 按凭证号打印
	 */
	@RequestMapping("/fa/ars/printVoucherCodeBillOut.action")
	@ResponseBody
	public Map<String, Object> printVoucherCodeBillOut(HttpSession session, int id,
			String reportName, String condition, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = billOutService.printVoucherCodeBillOut(id, caller, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/ars/submitBillOut.action")
	@ResponseBody
	public Map<String, Object> submitBillOut(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutService.submitBillOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/ars/resSubmitBillOut.action")
	@ResponseBody
	public Map<String, Object> resSubmitBillOut(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutService.resSubmitBillOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/ars/auditBillOut.action")
	@ResponseBody
	public Map<String, Object> auditBillOut(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutService.auditBillOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/ars/resAuditBillOut.action")
	@ResponseBody
	public Map<String, Object> resAuditBillOut(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutService.resAuditBillOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 记账
	 */
	@RequestMapping("/fa/ars/postBillOut.action")
	@ResponseBody
	public Map<String, Object> accountBillOut(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutService.accountedBillOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反记账
	 */
	@RequestMapping("/fa/ars/resPostBillOut.action")
	@ResponseBody
	public Map<String, Object> resAccountBillOut(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutService.resAccountedBillOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新税票信息
	 */
	@RequestMapping("/fa/ars/updateBillOutTaxcode.action")  
	@ResponseBody 
	public Map<String, Object> updateTaxcode(String caller, int bi_id, String bi_refno, String bi_remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billOutService.updateTaxcode(caller, bi_id, bi_refno, bi_remark);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 应收开票记录发票开具
	 */
	@RequestMapping("/fa/ars/openInvoice.action")  
	@ResponseBody 
	public Map<String, Object> openInvoice(String caller, int bi_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", billOutService.openInvoice(caller, bi_id));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 应收开票记录取消发票开具申请
	 */
	@RequestMapping("/fa/ars/cancelInvoiceApply.action")  
	@ResponseBody 
	public Map<String, Object> cancelInvoiceApply(String caller, int bi_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("resMsg", billOutService.cancelInvoiceApply(caller, bi_id));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 应收开票记录获取发票开具结果
	 */
	@RequestMapping("/fa/ars/queryInvoiceInfo.action")  
	@ResponseBody 
	public Map<String, Object> queryInvoiceInfo(String caller, int bi_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", billOutService.queryInvoiceInfo(caller, bi_id));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 应收开票记录获取发票开具结果
	 */
	@RequestMapping("/fa/ars/getTaxWebSite.action")  
	@ResponseBody 
	public Map<String, Object> getTaxWebSite() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("url", billOutService.getTaxWebSite());
		modelMap.put("success", true);
		return modelMap;
	}
}
