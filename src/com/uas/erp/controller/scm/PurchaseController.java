package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.b2b.model.PurchaseReply;
import com.uas.erp.core.BaseController;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.PurchaseService;

@Controller
public class PurchaseController extends BaseController {
	@Autowired
	private PurchaseService purchaseService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/purchase/savePurchase.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.savePurchase(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除采购单数据 包括采购明细
	 */
	@RequestMapping("/scm/purchase/deletePurchase.action")
	@ResponseBody
	public Map<String, Object> deletePurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.deletePurchase(id, caller);
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
	@RequestMapping("/scm/purchase/updatePurchase.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.updatePurchaseById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印采购单
	 */
	@RequestMapping("/scm/purchase/printPurchase.action")
	@ResponseBody
	public Map<String, Object> printPurchase(String caller, int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = purchaseService.printPurchase(id, caller, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 提交采购单
	 */
	@RequestMapping("/scm/purchase/submitPurchase.action")
	@ResponseBody
	public Map<String, Object> submitPurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.submitPurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交采购单
	 */
	@RequestMapping("/scm/purchase/resSubmitPurchase.action")
	@ResponseBody
	public Map<String, Object> resSubmitPurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.resSubmitPurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核采购单
	 */
	@RequestMapping("/scm/purchase/auditPurchase.action")
	@ResponseBody
	public Map<String, Object> auditPurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.auditPurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 同步采购单至B2B
	 */
	@RequestMapping("/scm/purchase/b2bPurchase.action")
	@ResponseBody
	public Map<String, Object> b2bPurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.b2bPurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核采购单
	 */
	@RequestMapping("/scm/purchase/resAuditPurchase.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.resAuditPurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/scm/purchase/getPrice.action")
	@ResponseBody
	public Map<String, Object> getPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.getPrice(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/scm/purchase/getStandardPrice.action")
	@ResponseBody
	public Map<String, Object> getStandardPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.getStandardPrice(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 已结案已作废采购单批量删除
	 */
	@RequestMapping("/scm/purchase/vastDeletePurc.action")
	@ResponseBody
	public Map<String, Object> vastDeletePurc(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.vastDeletePurc(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 采购批量反结案
	 */
	@RequestMapping(value = "/scm/purchase/vastResClosePurchaseDetail.action")
	@ResponseBody
	public Map<String, Object> vastClosePurchaseDetail(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.vastClosePurchaseDetail(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 复制采购单
	 * 
	 * @param session
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("/scm/purchase/copyPurchase.action")
	@ResponseBody
	public Map<String, Object> copyPurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", purchaseService.copyPurchase(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改供应商回复信息
	 * */
	@RequestMapping("scm/purchase/updateVendorBackInfo.action")
	@ResponseBody
	public Map<String, Object> updateVendorBackInfo(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.updateVendorBackInfo(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取委外商价格最低价信息
	 */
	@RequestMapping("/scm/purchase/getMakeVendorPrice.action")
	@ResponseBody
	public Map<String, Object> getMakeVendorPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.getMakeVendorPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取委外商价格信息（根据前台传回的供应商取价）
	 */
	@RequestMapping("/scm/purchase/getVendorPrice.action")
	@ResponseBody
	public Map<String, Object> getVendorPrice(String caller, int id, String vendcode, String curr) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.getVendorPrice(id, vendcode, curr, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 按上次委外单  取价
	 */
	@RequestMapping("/scm/purchase/getLastMakePrice.action")
	@ResponseBody
	public Map<String, Object> getLastMakePrice(String caller, int id,String prodcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.getLastMakePrice(id, caller,prodcode);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 万利达，采购单同步到香港系统
	 */
	@RequestMapping("/scm/purchase/synctohk.action")
	@ResponseBody
	public Map<String, Object> syncPurc(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.syncPurc(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 万利达，采购单刷新同步状态
	 */
	@RequestMapping("/scm/purchase/syncstatus.action")
	@ResponseBody
	public Map<String, Object> resetSyncStatus(String caller, Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.resetSyncStatus(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 刷新采购单已收料数量
	 */
	@RequestMapping("/scm/purchase/refreshqty.action")
	@ResponseBody
	public Map<String, Object> refreshqty(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.refreshqty(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交采购单
	 */
	@RequestMapping("/scm/purchase/purchasedataupdate.action")
	@ResponseBody
	public Map<String, Object> purchasedataupdate(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.purchasedataupdate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 分拆采购单
	 */
	@RequestMapping("scm/purchase/splitPurchase.action")
	@ResponseBody
	public Map<String, Object> splitPurchase(String caller, String formdata, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.splitPurchase(formdata, data, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @author wsy
	 * 
	 */
	@RequestMapping("scm/purchase/dataReply.action")
	@ResponseBody
	public Map<String, Object> dataReply(String pucode,String detno,String qty,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.dataReply(pucode,detno,qty,data);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 查找回复记录
	 * 
	 * @param id
	 *            采购订单ID
	 */
	@RequestMapping("scm/purchase/getReply.action")
	@ResponseBody
	public List<PurchaseReply> getReply(int id) {
		return purchaseService.findReplyByPuid(id);
	}
	
	/**
	 * 采购单结案
	 */
	@RequestMapping("/scm/purchase/endPurchase.action")
	@ResponseBody
	public Map<String, Object> endPurchase(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.endPurchase(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转存采购单之前
	 */
	@RequestMapping("/scm/purchase/TurnBankRegister.action")
	@ResponseBody
	public Map<String, Object> turnBankRegister(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.turnBankRegister(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 确定转存采购单
	 */
	@RequestMapping("/scm/purchase/ConfirmTurnBankRegister.action")
	@ResponseBody
	public Map<String, Object> confirmTurnBankRegister(String formStore,String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String str = purchaseService.confirmTurnBankRegister(formStore,gridStore);
		modelMap.put("msg", str);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取采购合同关联流程历史
	 */
	@RequestMapping("/scm/purchase/getContractProcess.action")
	@ResponseBody
	public Map<String, Object> getContractProcess(int id) {
		Map<String, Object> modelMap = purchaseService.getContractProcess(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 批量修改回复日期
	 */
	@RequestMapping("/scm/purchase/updateGridDetailReplyDate.action")
	@ResponseBody
	public Map<String, Object> updateGridDetailReplyDate(String date, String id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseService.updateGridDetailReplyDate(id,date);
		modelMap.put("success", true);
		return modelMap;
	}
}
