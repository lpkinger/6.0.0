package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.VerifyApplyService;

@Controller
public class VerifyApplyController extends BaseController {
	@Autowired
	private VerifyApplyService verifyApplyService;

	/**
	 * 保存
	 */
	@RequestMapping("/scm/purchase/saveVerifyApply.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyService.saveVerifyApply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/scm/purchase/deleteVerifyApply.action")
	@ResponseBody
	public Map<String, Object> deleteVerifyApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyService.deleteVerifyApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/scm/purchase/updateVerifyApply.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyService.updateVerifyApply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/scm/purchase/printVerifyApply.action")
	@ResponseBody
	public Map<String, Object> printVerifyApply(int id, String reportName, String condition, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = verifyApplyService.printVerifyApply(id, reportName, condition, caller);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitVerifyApply.action")
	@ResponseBody
	public Map<String, Object> submitVerifyApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyService.submitVerifyApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitVerifyApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitVerifyApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyService.resSubmitVerifyApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditVerifyApply.action")
	@ResponseBody
	public Map<String, Object> auditVerifyApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyService.auditVerifyApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditVerifyApply.action")
	@ResponseBody
	public Map<String, Object> resAuditVerifyApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyService.resAuditVerifyApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转采购验收单
	 */
	@RequestMapping("/scm/purchase/turnStorage.action")
	@ResponseBody
	public Map<String, Object> turnProdio(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", verifyApplyService.turnStorage(caller, id));
		modelMap.put("success", true);
		return modelMap;
	}	

	/**
	 * 转采购检验单
	 */
	@RequestMapping("/scm/purchase/turnIQC.action")
	@ResponseBody
	public Map<String, Object> vastTurnIQC(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", verifyApplyService.detailTurnIQC(data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转委外检验单
	 */
	@RequestMapping("/pm/make/turnFQC.action")
	@ResponseBody
	public Map<String, Object> vastTurnFQC(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", verifyApplyService.detailTurnFQC(data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 分装单据
	 */
	@RequestMapping("/scm/purchase/Subpackage.action")
	@ResponseBody
	public Map<String, Object> Subpackage(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String logString = verifyApplyService.Subpackage(id);
		modelMap.put("success", true);
		modelMap.put("log", logString);
		return modelMap;
	}

	/**
	 * 产生批号
	 * */
	@RequestMapping("/scm/purchase/ProduceBatch.action")
	@ResponseBody
	public Map<String, Object> ProduceBatch(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyService.ProduceBatch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 清除分装明细
	 */
	@RequestMapping("/scm/purchase/ClearSubpackage.action")
	@ResponseBody
	public Map<String, Object> ClearSubpackage(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String logString = verifyApplyService.ClearSubpackage(id);
		modelMap.put("success", true);
		modelMap.put("log", logString);
		return modelMap;
	}

	/**
	 * 条码打印（整单）
	 */
	@RequestMapping("/scm/purchase/printBar.action")
	@ResponseBody
	public Map<String, Object> printBar(int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = verifyApplyService.printBar(id, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
	
	/**
	 * 生成条码
	 * @param formStore
	 * @return
	 */
	@RequestMapping("/scm/purchase/generateBarcode.action")
	@ResponseBody
	public Map<String, Object> generateBarcode(String caller,String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyService.generateBarcode(caller,formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 批量生成条码
	 * @param caller
	 * @param formStore
	 * @return
	 */
	@RequestMapping("/scm/purchase/batchGenBarcode.action")
	@ResponseBody
	public Map<String, Object> batchGenBarcode(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    verifyApplyService.batchGenBarcode(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 * 批量生成条码和箱号
	 * @param caller
	 * @param formStore
	 * @return
	 */
	@RequestMapping("/scm/purchase/BatchGenBO.action")
	@ResponseBody
	public Map<String, Object> atchGenBO(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    verifyApplyService.batchGenBO(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
    /**
     * 保存条码
     * @param gridStore
     * @param caller
     * @return
     */
	@RequestMapping("/scm/purchase/saveBarcodeDetail.action")
	@ResponseBody
	public Map<String, Object> saveBarcodeDetail(String caller,String gridStore ) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    verifyApplyService.saveBarcodeDetail(caller, gridStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除条码维护中产生的所有条码明细
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/scm/purchase/deleteAllBarDetails.action")
	@ResponseBody
	public Map<String, Object> deleteAllBarDetails(String caller,String code ,int detno ) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    verifyApplyService.deleteAllBarDetails(caller, code,detno);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 分拆收料单
	 */
	@RequestMapping("scm/purchase/splitVerifyApply.action")
	@ResponseBody
	public Map<String, Object> splitVerifyApply(String caller, String formdata, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyService.splitVerifyApply(formdata, data, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
