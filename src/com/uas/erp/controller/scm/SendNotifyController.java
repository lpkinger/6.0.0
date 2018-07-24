package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.common.SingleGridPanelService;
import com.uas.erp.service.scm.SendNotifyService;

@Controller
public class SendNotifyController extends BaseController {
	@Autowired
	private SendNotifyService sendNotifyService;
	@Autowired
	private SingleGridPanelService singleGridPanelService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/sale/saveSendNotify.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyService.saveSendNotify(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存船务信息
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/scm/sale/saveShip.action")
	@ResponseBody
	public Map<String, Object> saveShip(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyService.saveShip(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/sale/deleteSendNotify.action")
	@ResponseBody
	public Map<String, Object> deleteSendNotify(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyService.deleteSendNotify(id, caller);
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
	@RequestMapping("/scm/sale/updateSendNotify.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyService.updateSendNotifyById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitSendNotify.action")
	@ResponseBody
	public Map<String, Object> submitSendNotify(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyService.submitSendNotify(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitSendNotify.action")
	@ResponseBody
	public Map<String, Object> resSubmitSendNotify(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyService.resSubmitSendNotify(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditSendNotify.action")
	@ResponseBody
	public Map<String, Object> auditSendNotify(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyService.auditSendNotify(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditSendNotify.action")
	@ResponseBody
	public Map<String, Object> resAuditSendNotify(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyService.resAuditSendNotify(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印发货通知单
	 */
	@RequestMapping("/scm/sale/printSendNotify.action")
	@ResponseBody
	public Map<String, Object> print(String caller, int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = sendNotifyService.printSendNotify(id, caller, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 转出货单
	 */
	@RequestMapping("/scm/sale/turnProdIO.action")
	@ResponseBody
	public Map<String, Object> turnProdIO(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int piid = sendNotifyService.turnProdIO(id, caller);
		modelMap.put("id", piid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 计算可用量
	 */
	@RequestMapping("/scm/sale/loadOnHandQty.action")
	@ResponseBody
	public Map<String, Object> loadOnHandQty(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyService.loadOnHandQty(id);
		GridPanel gridPanel = singleGridPanelService.getGridPanelByCaller(caller, "snd_snid=" + id, null, null, 1,false,"");
		if (gridPanel != null) {
			modelMap.put("data", gridPanel.getDataString());
		}
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("scm/sale/SendNotifyBatch.action")
	@ResponseBody
	public Map<String, Object> splitSale(String caller, String formdata, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyService.splitSendtify(formdata, data, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
