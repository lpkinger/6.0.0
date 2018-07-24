package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakePrepareService;

@Controller
public class MakePrepareController extends BaseController {
	@Autowired
	private MakePrepareService makePrepareService;
	
	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/saveMakePrepare.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePrepareService.saveMakePrepare(formStore, caller);
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
	@RequestMapping("/pm/mes/updateMakePrepare.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePrepareService.updateMakePrepareById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/pm/mes/deleteMakePrepare.action")
	@ResponseBody
	public Map<String, Object> deleteMakePrepare(int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePrepareService.deleteMakePrepare(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitMakePrepare.action")
	@ResponseBody
	public Map<String, Object> submitMakePrepare(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePrepareService.submitMakePrepare(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitMakePrepare.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakePrepare(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePrepareService.resSubmitMakePrepare(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditMakePrepare.action")
	@ResponseBody
	public Map<String, Object> auditMakePrepareio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePrepareService.auditMakePrepare(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditMakePrepare.action")
	@ResponseBody
	public Map<String, Object> resAuditMakePrepareio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePrepareService.resAuditMakePrepare(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/mes/getBar.action")
	@ResponseBody
	public Map<String, Object> getBar(String barcode, String whcode, int maid, int mpid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("message", makePrepareService.getBar(barcode, whcode, maid, mpid));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/mes/returnBar.action")
	@ResponseBody
	public Map<String, Object> returnBar(String barcode, int mpid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("message",makePrepareService.returnBar(barcode, mpid));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 *转领料单
	 * @param id
	 * @return
	 */
	@RequestMapping("/pm/mes/toProdIOGet.action")
	@ResponseBody
	public Map<String, Object> toProdIOGet(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePrepareService.toProdIOGet(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
