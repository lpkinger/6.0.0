package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.CustomPageService;

@Controller
public class CustomPageController {
	@Autowired
	private CustomPageService customPageService;
	/*
	 * 批准
	 */
	@RequestMapping("/custom/aprovePage.action")
	@ResponseBody
	public Map<String, Object> aprovePage(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.approvePage(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交(批准)
	 */
	@RequestMapping("/custom/submitapproves.action")
	@ResponseBody
	public Map<String, Object> submitApproves(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.submitApproves(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转银行登记
	 */
	@RequestMapping("/custom/turnBankRegister.action")
	@ResponseBody
	public Map<String, Object> turnBankRegister(int id, String paymentcode, String payment, double thispayamount) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("log", customPageService.turnBankRegister(id, paymentcode, payment, thispayamount));
		return modelMap;
	}


	/**
	 * 反提交(批准)
	 */
	@RequestMapping("/custom/ressubmitapproves.action")
	@ResponseBody
	public Map<String, Object> resSubmitApproves(String caller,
			int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.resSubmitApproves(caller, id);
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
	@RequestMapping("/custom/savePage.action")
	@ResponseBody
	public Map<String, Object> save(String caller,
			String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.savePage(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/custom/deletePage.action")
	@ResponseBody
	public Map<String, Object> deletePage(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.deletePage(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/custom/updatePage.action")
	@ResponseBody
	public Map<String, Object> update(String caller,
			String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.updatePageById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印采购单
	 */
	@RequestMapping("/custom/printPage.action")
	@ResponseBody
	public Map<String, Object> printPage(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.printPage(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/custom/submitPage.action")
	@ResponseBody
	public Map<String, Object> submitPage(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.submitPage(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/custom/resSubmitPage.action")
	@ResponseBody
	public Map<String, Object> resSubmitPage(String caller,
			int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.resSubmitPage(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 * @throws Exception 
	 */
	@RequestMapping("/custom/auditPage.action")
	@ResponseBody
	public Map<String, Object> auditPage(String caller, int id) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.auditPage( id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/custom/resAuditPage.action")
	@ResponseBody
	public Map<String, Object> resAuditPage(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.resAuditPage(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 禁用
	 */
	@RequestMapping("/custom/bannedPage.action")
	@ResponseBody
	public Map<String, Object> bannedCurrencys(String caller,
			int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.bannedPage(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反禁用
	 */
	@RequestMapping("/custom/resBannedPage.action")
	@ResponseBody
	public Map<String, Object> resBannedCurrencys(String caller,
			int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.resBannedPage(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/custom/postPage.action")
	@ResponseBody
	public Map<String, Object> postPage(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.postPage(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确认,操作是确认从B2B下载来的PO，确认除了更新确认状态，还要更新相关字段，eg:录入人，客户/供应商编号等
	 */
	@RequestMapping("/custom/ConfirmPage.action")
	@ResponseBody
	public Map<String, Object> onConfirmPage(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.confirmPage(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 判断当前表单 是否配置了列表
	 * */
	@RequestMapping("/custom/IfDatalist.action")
	@ResponseBody
	public Map<String, Object> IfDatalist(String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.IfDatalist(caller);
		modelMap.put("success", true);
		return modelMap;

	}

	/**
	 * 根据form 配置生成datalist
	 * */
	@RequestMapping("/custom/ToDatalistByForm.action")
	@ResponseBody
	public Map<String, Object> ToDataLisByForm(String caller, String type) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.ToDataListByForm(caller, type);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 确认
	 */
	@RequestMapping("oa/custom/confirm.action")
	@ResponseBody
	public Map<String, Object> confirm(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.confirm( id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 确认
	 */
	@RequestMapping("oa/custom/resConfirm.action")
	@ResponseBody
	public Map<String, Object> resConfirm(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.resConfirm( id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转单 
	 */
	@RequestMapping("/custom/turnPage.action")
	@ResponseBody
	public Map<String, Object> turnPage(int id,String caller,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.turnPage(id,caller,data);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转单 
	 */
	@RequestMapping("oa/custom/turnDocPage.action")
	@ResponseBody
	public Map<String, Object> turnDocPage(int id,String caller,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", customPageService.turnDocPage(id,caller,data));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 結案
	 */
	@RequestMapping("/custom/endPage.action")
	@ResponseBody
	public Map<String, Object> endPage(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.endPage(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反結案
	 */
	@RequestMapping("/custom/resEndPage.action")
	@ResponseBody
	public Map<String, Object> resEndPage(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customPageService.resEndPage(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}	
	
	
	
	
	
	
	
}
