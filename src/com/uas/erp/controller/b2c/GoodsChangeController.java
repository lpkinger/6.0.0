package com.uas.erp.controller.b2c;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.b2c.GoodsChangeService;

@Controller
public class GoodsChangeController extends BaseController{
	@Autowired
	private GoodsChangeService goodsChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mps/saveGoodsChange.action")
	@ResponseBody
	public Map<String, Object> saveGoodsChange(String caller, String formStore,String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsChangeService.saveGoodsChange(formStore, caller,param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/pm/mps/deleteGoodsChange.action")
	@ResponseBody
	public Map<String, Object> deleteGoodsChange(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	goodsChangeService.deleteGoodsChange(id, caller);
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
	@RequestMapping("/pm/mps/updateGoodsChange.action")
	@ResponseBody
	public Map<String, Object> updateGoodsChangeById(String caller, String formStore,String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsChangeService.updateGoodsChangeById(formStore, caller,param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mps/submitGoodsChange.action")
	@ResponseBody
	public Map<String, Object> submitGoodsChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsChangeService.submitGoodsChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mps/resSubmitGoodsChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitGoodsChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsChangeService.resSubmitGoodsChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mps/auditGoodsChange.action")
	@ResponseBody
	public Map<String, Object> auditGoodsChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsChangeService.auditGoodsChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mps/resAuditGoodsChange.action")
	@ResponseBody
	public Map<String, Object> resAuditGoodsChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsChangeService.resAuditGoodsChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转拨出
	 */
	@RequestMapping("/pm/mps/goodsChangeTurnOut.action")
	@ResponseBody
	public Map<String, Object> turnAppropriationOut(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", goodsChangeService.turnAppropriationOut(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

}
