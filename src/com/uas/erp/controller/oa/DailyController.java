package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.oa.DailyService;

@Controller
public class DailyController extends BaseController {
	@Autowired
	private DailyService DailyService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/Daily/saveDaily.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.saveDaily(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除采购单数据 包括采购明细
	 */
	@RequestMapping("/oa/Daily/deleteDaily.action")
	@ResponseBody
	public Map<String, Object> deleteDaily(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.deleteDaily(id, caller);
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
	@RequestMapping("/oa/Daily/updateDaily.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.updateDailyById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印采购单
	 */
	@RequestMapping("/oa/Daily/printDaily.action")
	@ResponseBody
	public Map<String, Object> printDaily(String caller, int id,
			String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = DailyService.printDaily(id, caller, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 提交采购单
	 */
	@RequestMapping("/oa/Daily/submitDaily.action")
	@ResponseBody
	public Map<String, Object> submitDaily(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.submitDaily(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交采购单
	 */
	@RequestMapping("/oa/Daily/resSubmitDaily.action")
	@ResponseBody
	public Map<String, Object> resSubmitDaily(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.resSubmitDaily(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核采购单
	 */
	@RequestMapping("/oa/Daily/auditDaily.action")
	@ResponseBody
	public Map<String, Object> auditDaily(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.auditDaily(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核采购单
	 */
	@RequestMapping("/oa/Daily/resAuditDaily.action")
	@ResponseBody
	public Map<String, Object> resAuditDaily(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.resAuditDaily(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 结案
	 */
	@RequestMapping("/oa/Daily/endDaily.action")
	@ResponseBody
	public Map<String, Object> endDaily(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.endDaily(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/oa/Daily/resEndDaily.action")
	@ResponseBody
	public Map<String, Object> resEndDaily(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.resEndDaily(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/Daily/getPrice.action")
	@ResponseBody
	public Map<String, Object> getPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.getPrice(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/Daily/getStandardPrice.action")
	@ResponseBody
	public Map<String, Object> getStandardPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.getStandardPrice(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 已结案已作废采购单批量删除
	 */
	@RequestMapping("/oa/Daily/vastDeletedaily.action")
	@ResponseBody
	public Map<String, Object> vastDeletedaily(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.vastDeleteDaily(id, caller);
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
	@RequestMapping("/oa/Daily/copyDaily.action")
	@ResponseBody
	public Map<String, Object> copyDaily(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", DailyService.copyDaily(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改供应商回复信息
	 * */
	@RequestMapping("oa/Daily/updateVendorBackInfo.action")
	@ResponseBody
	public Map<String, Object> updateVendorBackInfo(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.updateVendorBackInfo(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取委外商价格信息
	 */
	@RequestMapping("/oa/Daily/getMakeVendorPrice.action")
	@ResponseBody
	public Map<String, Object> getMakeVendorPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.getMakeVendorPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 万利达，采购单同步到香港系统
	 */
	@RequestMapping("/oa/Daily/synctohk.action")
	@ResponseBody
	public Map<String, Object> syncdaily(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.syncDaily(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 万利达，采购单刷新同步状态
	 */
	@RequestMapping("/oa/Daily/syncstatus.action")
	@ResponseBody
	public Map<String, Object> resetSyncStatus(String caller, Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyService.resetSyncStatus(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

}
