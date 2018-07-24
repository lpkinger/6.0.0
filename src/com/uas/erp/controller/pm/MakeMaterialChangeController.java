package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeMaterialChangeService;

@Controller
public class MakeMaterialChangeController extends BaseController {
	@Autowired
	private MakeMaterialChangeService makeMaterialChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/saveMakeMaterialChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialChangeService.saveMakeMaterialChange(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/make/deleteMakeMaterialChange.action")
	@ResponseBody
	public Map<String, Object> deleteMakeMaterialChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialChangeService.deleteMakeMaterialChange(id, caller);
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
	@RequestMapping("/pm/make/updateMakeMaterialChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialChangeService.updateMakeMaterialChangeById(formStore,
				param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitMakeMaterialChange.action")
	@ResponseBody
	public Map<String, Object> submitMoveProductDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialChangeService.submitMakeMaterialChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitMakeMaterialChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeMaterialChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialChangeService.resSubmitMakeMaterialChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditMakeMaterialChange.action")
	@ResponseBody
	public Map<String, Object> auditMakeMaterialChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialChangeService.auditMakeMaterialChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditMakeMaterialChange.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeMaterialChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialChangeService.resAuditMakeMaterialChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新ECN
	 * */
	@RequestMapping("/pm/make/updateMakeMaterialChangeInProcss.action")
	@ResponseBody
	public Map<String, Object> updateMakeMaterialChangeInProcss(String caller,
			String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialChangeService.updateMakeMaterialChangeInProcss(formStore,
				param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 明细行转取消执行
	 * */
	@RequestMapping("/pm/make/MakeMaterialChangeCloseDet.action")
	@ResponseBody
	public Map<String, Object> MakeMaterialChangeCloseDet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialChangeService.MakeMaterialChangeCloseDet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 明细行转执行
	 * */
	@RequestMapping("/pm/make/MakeMaterialChangeOpenDet.action")
	@ResponseBody
	public Map<String, Object> MakeMaterialChangeOpenDet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialChangeService.MakeMaterialChangeOpenDet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 全部取消执行
	 * */
	@RequestMapping("/pm/make/MakeMaterialChangeCloseAll.action")
	@ResponseBody
	public Map<String, Object> MakeMaterialChangeCloseAll(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialChangeService.MakeMaterialChangeCloseAll(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转退料单
	 * */
	@RequestMapping("/pm/make/makeMaterialChangeTurnProdIOReturn.action")
	@ResponseBody
	public Map<String, Object>makeMaterialChangeTurnProdIOReturn(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",makeMaterialChangeService.makeMaterialChangeTurnProdIOReturn(id,caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
}
