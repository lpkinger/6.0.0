package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.PreRecService;

@Controller("preRecController")
public class PreRecController extends BaseController {
	@Autowired
	private PreRecService preRecService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/PreRecController/savePreRec.action")
	@ResponseBody
	public Map<String, Object> savePreRec(String formStore, String param, String param2, String param3, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		preRecService.savePreRec(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 */
	@RequestMapping("/fa/PreRecController/deletePreRec.action")
	@ResponseBody
	public Map<String, Object> deletePreRec(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		preRecService.deletePreRec(caller, id);
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
	@RequestMapping("/fa/PreRecController/updatePreRec.action")
	@ResponseBody
	public Map<String, Object> updatePreRec(String formStore, String param, String param2, String param3, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		preRecService.updatePreRecById(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	// 在明细行修改前判断本次发票数量是否发生改变

	// /**
	// * 打印采购单
	// */
	// @RequestMapping("/fa/ars/printARBill.action")
	// @ResponseBody
	// public Map<String, Object> printARBill(int id,String
	// caller) {
	//
	//
	// Map<String, Object> modelMap = new HashMap<String, Object>();
	// arBillService.printARBill(caller,id);
	// modelMap.put("success", true);
	// return modelMap;
	// }
	/**
	 * 提交采购单
	 */
	@RequestMapping("/fa/PreRecController/submitPreRec.action")
	@ResponseBody
	public Map<String, Object> submitPreRec(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		preRecService.submitPreRec(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交采购单
	 */
	@RequestMapping("/fa/PreRecController/resSubmitPreRec.action")
	@ResponseBody
	public Map<String, Object> resSubmitPreRec(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		preRecService.resSubmitPreRec(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核采购单
	 */
	@RequestMapping("/fa/PreRecController/auditPreRec.action")
	@ResponseBody
	public Map<String, Object> auditPreRec(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		preRecService.auditPreRec(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核采购单
	 */
	@RequestMapping("/fa/PreRecController/resAuditPreRec.action")
	@ResponseBody
	public Map<String, Object> resAuditPreRec(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		preRecService.resAuditPreRec(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PreRecController/postPreRec.action")
	@ResponseBody
	public Map<String, Object> postPreRec(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		preRecService.postPreRec(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PreRecController/resPostPreRec.action")
	@ResponseBody
	public Map<String, Object> resPostPreRec(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preRecService.resPostPreRec(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 业务员转预收
	 */
	@RequestMapping("/fa/ars/sellerPreRec.action")
	@ResponseBody
	public Map<String, Object> sellerPreRec(String caller, int id, String emcode, String thisamount) {
		return success(preRecService.sellerPreRec(id, emcode, thisamount, caller));
	}
}
