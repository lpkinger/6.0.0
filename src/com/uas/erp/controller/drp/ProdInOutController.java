package com.uas.erp.controller.drp;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.ProdInOutService;

@Controller
public class ProdInOutController extends BaseController {
	@Autowired
	private ProdInOutService prodInOutService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/drp/distribution/saveProdInOut.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.saveProdInOut(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 注意:prodInOut与其他单据不同，需要将caller也传回后台
	 */
	@RequestMapping("/drp/distribution/deleteProdInOut.action")
	@ResponseBody
	public Map<String, Object> deleteProdIo(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.deleteProdInOut(caller, id);
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
	@RequestMapping("/drp/distribution/updateProdInOut.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.updateProdInOutById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/drp/distribution/printProdInOut.action")
	@ResponseBody
	public Map<String, Object> printPurchase(int id, String caller,
			String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = prodInOutService.printProdInOut(caller, id, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/distribution/submitProdInOut.action")
	@ResponseBody
	public Map<String, Object> submitPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.submitProdInOut(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/distribution/resSubmitProdInOut.action")
	@ResponseBody
	public Map<String, Object> resSubmitPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.resSubmitProdInOut(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/distribution/auditProdInOut.action")
	@ResponseBody
	public Map<String, Object> auditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.auditProdInOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/distribution/resAuditProdInOut.action")
	@ResponseBody
	public Map<String, Object> resAuditProdInOut(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.resAuditProdInOut(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/drp/distribution/postProdInOut.action")
	@ResponseBody
	public Map<String, Object> postProdInOut(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.postProdInOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/drp/distribution/resPostProdInOut.action")
	@ResponseBody
	public Map<String, Object> resPostProdInOut(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.resPostProdInOut(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
}
