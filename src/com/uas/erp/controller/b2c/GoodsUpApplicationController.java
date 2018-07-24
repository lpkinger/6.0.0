package com.uas.erp.controller.b2c;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.b2c.GoodsUpApplicationService;

@Controller
public class GoodsUpApplicationController extends BaseController{
	@Autowired
	private GoodsUpApplicationService goodsUpApplicationService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mps/saveGoodsUpApplication.action")
	@ResponseBody
	public Map<String, Object> saveGoodsUpApplication(String caller, String formStore,String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsUpApplicationService.saveGoodsUpApplication(formStore, caller,param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/pm/mps/deleteGoodsUpApplication.action")
	@ResponseBody
	public Map<String, Object> deleteGoodsUpApplication(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	goodsUpApplicationService.deleteGoodsUpApplication(id, caller);
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
	@RequestMapping("/pm/mps/updateGoodsUpApplication.action")
	@ResponseBody
	public Map<String, Object> updateGoodsUpApplicationById(String caller, String formStore,String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsUpApplicationService.updateGoodsUpApplicationById(formStore, caller,param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mps/submitGoodsUpApplication.action")
	@ResponseBody
	public Map<String, Object> submitGoodsUpApplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsUpApplicationService.submitGoodsUpApplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mps/resSubmitGoodsUpApplication.action")
	@ResponseBody
	public Map<String, Object> resSubmitGoodsUpApplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsUpApplicationService.resSubmitGoodsUpApplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mps/auditGoodsUpApplication.action")
	@ResponseBody
	public Map<String, Object> auditGoodsUpApplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsUpApplicationService.auditGoodsUpApplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mps/resAuditGoodsUpApplication.action")
	@ResponseBody
	public Map<String, Object> resAuditGoodsUpApplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsUpApplicationService.resAuditGoodsUpApplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 拆分明细
	 */
	@RequestMapping("/pm/mps/splitDetail.action")
	@ResponseBody
	public Map<String, Object> splitDetail(String formdata, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsUpApplicationService.splitDetail(formdata, data);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转拨出
	 */
	@RequestMapping("/pm/mps/goodsUpTurnOut.action")
	@ResponseBody
	public Map<String, Object> turnAppropriationOut(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", goodsUpApplicationService.turnAppropriationOut(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 维护标准料号
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/pm/mps/getUUId.action")
	@ResponseBody
	public Map<String, Object> getUUId(String caller, int id) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("code",goodsUpApplicationService.getUUId(id, caller));	
		modelMap.put("success", true);
	    return modelMap;
	}
	
	/**
	 * 如果自动上架不成功手动点击上架
	 */
	@RequestMapping("/pm/mps/goodsUp.action")
	@ResponseBody
	public Map<String, Object> goodsUp(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", goodsUpApplicationService.goodsUp(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
