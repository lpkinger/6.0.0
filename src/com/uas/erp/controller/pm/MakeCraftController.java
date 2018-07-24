package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeCraftService;

@Controller
public class MakeCraftController extends BaseController {
	@Autowired
	private MakeCraftService makeCraftService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/saveMakeCraft.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeCraftService.saveMakeCraft(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/mes/deleteMakeCraft.action")
	@ResponseBody
	public Map<String, Object> deleteMakeCraft(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeCraftService.deleteMakeCraft(id, caller);
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
	@RequestMapping("/pm/mes/updateMakeCraft.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeCraftService.updateMakeCraftById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/pm/mes/printMakeCraft.action")
	@ResponseBody
	public Map<String, Object> printMakeCraft(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeCraftService.printMakeCraft(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitMakeCraft.action")
	@ResponseBody
	public Map<String, Object> submitMakeCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeCraftService.submitMakeCraft(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitMakeCraft.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeCraftService.resSubmitMakeCraft(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditMakeCraft.action")
	@ResponseBody
	public Map<String, Object> auditMakeCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeCraftService.auditMakeCraft(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditMakeCraft.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeCraftService.resAuditMakeCraft(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/pm/mes/getMakeCraft.action")
	@ResponseBody
	public Map<String, Object> getMakeCraft(String caller) {
		return makeCraftService.getMakeCraft(caller);
	}
	
	/**
	 * 结案
	 */
	@RequestMapping("/pm/mes/endMakeCraft.action")
	@ResponseBody
	public Map<String, Object> endMakeCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeCraftService.endMakeCraft(caller , id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 强制结案
	 */
	@RequestMapping("/pm/mes/forceEndMakeCraft.action")
	@ResponseBody
	public Map<String, Object> forceEndMakeCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeCraftService.forceEndMakeCraft(caller , id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/pm/mes/resEndMakeCraft.action")
	@ResponseBody
	public Map<String, Object> resEndMakeCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeCraftService.resEndMakeCraft(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 *车间排程 - 工序转移
	 */
	@RequestMapping(value = "/pm/mes/vastTurnCraftTransfer.action")
	@ResponseBody
	public Map<String, Object> vastTurnCraftTransfer(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = makeCraftService.vastTurnCraftTransfer(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 *车间排程 - 工序跳转
	 */
	@RequestMapping(value = "/pm/mes/vastTurnCraftJump.action")
	@ResponseBody
	public Map<String, Object> vastTurnCraftJump(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = makeCraftService.vastTurnCraftJump(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 *车间排程 - 完工入库
	 */
	@RequestMapping(value = "/pm/mes/vastTurnMadeIN.action")
	@ResponseBody
	public Map<String, Object> vastTurnMadeIN(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = makeCraftService.vastTurnMadeIN(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 *车间排程 - 工序退制
	 */
	@RequestMapping(value = "/pm/mes/vastTurnCraftBack.action")
	@ResponseBody
	public Map<String, Object> vastTurnCraftBack(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = makeCraftService.vastTurnCraftBack(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 *车间排程 - 工序退料
	 */
	@RequestMapping(value = "/pm/mes/vastTurnCraftReturn.action")
	@ResponseBody
	public Map<String, Object> vastTurnCraftReturn(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = makeCraftService.vastTurnCraftReturn(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 *车间排程 - 工序报废
	 */
	@RequestMapping(value = "/pm/mes/vastTurnCraftScrap.action")
	@ResponseBody
	public Map<String, Object> vastTurnCraftScrap(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = makeCraftService.vastTurnCraftScrap(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改委外信息  
	 * 
	 * @param vend
	 *            委外商号
	 * @param curr
	 *            币别
	 * @param taxr
	 *            税率
	 * @param price
	 *            加工单价
	 * @param ma_servicer
	 *            是否免费加工
	 */
	@RequestMapping("/pm/mes/updateOSInfoVendor.action")
	@ResponseBody
	public Map<String, Object> updateOSInfoVendor(String caller, Integer id, String vend, String curr, String taxr, String price, String paymc,
			String mc_servicer, String paym, String remark, String apvend) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeCraftService.updateOSVendor(id, vend, curr, taxr, price, paymc, paym, mc_servicer, remark, apvend, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
