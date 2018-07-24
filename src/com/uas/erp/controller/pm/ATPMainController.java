package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ATPMainService;

@Controller
public class ATPMainController extends BaseController {
	@Autowired
	private ATPMainService atpMainService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/atp/saveATPMain.action")
	@ResponseBody
	public Map<String, Object> saveATPMain(String formStore,String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		atpMainService.saveATPMain(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/atp/deleteATPMain.action")
	@ResponseBody
	public Map<String, Object> deleteATPMain(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		atpMainService.deleteATPMain(id,caller);
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
	@RequestMapping("/pm/atp/updateATPMain.action")
	@ResponseBody
	public Map<String, Object> updateATPMain(String formStore,String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		atpMainService.updateATPMainById(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/pm/atp/printATPMain.action")
	@ResponseBody
	public Map<String, Object> printATPMain(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		atpMainService.printATPMain(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/atp/submitATPMain.action")
	@ResponseBody
	public Map<String, Object> submitATPMain(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		atpMainService.submitATPMain(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/atp/resSubmitATPMain.action")
	@ResponseBody
	public Map<String, Object> resSubmitATPMain(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		atpMainService.resSubmitATPMain(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/atp/auditATPMain.action")
	@ResponseBody
	public Map<String, Object> auditATPMain(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		atpMainService.auditATPMain(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/atp/resAuditATPMain.action")
	@ResponseBody
	public Map<String, Object> resAuditATPMain(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		atpMainService.resAuditATPMain(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 执行运算
	 */
	@RequestMapping("/pm/atp/executeOperation.action")
	@ResponseBody
	public Map<String, Object> executeOperation(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		atpMainService.executeOperation(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 自动装载需求到ATP计划，自动运算ATP，返回ATPID
	 */
	@RequestMapping("/pm/atp/runATPFromOther.action")
	@ResponseBody
	public Map<String, Object> runATPFromOther(String fromcode,String caller, String fromwhere) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int atpid = atpMainService.runATPFromOther(fromcode, fromwhere,caller);
		modelMap.put("success", true);
		modelMap.put("atpid", atpid);
		return modelMap;
	}

	/**
	 * 装载销售订单
	 * */
	@RequestMapping("/pm/atp/loadSale.action")
	@ResponseBody
	public Map<String, Object> loadSale(String caller, 
			String data, int am_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		atpMainService.loadSale(caller, data ,am_id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 装载全部销售订单
	 * */
	@RequestMapping("pm/atp/loadAllSale.action")
	@ResponseBody
	public Map<String, Object> loadAllSale(String caller,
			String condition, int am_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		atpMainService.loadAllSale(caller, am_id, condition);
		modelMap.put("success", true);
		return modelMap;
	}

}
