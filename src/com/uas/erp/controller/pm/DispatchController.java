package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.DispatchService;

@Controller
public class DispatchController extends BaseController {
	@Autowired
	private DispatchService dispatchService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/saveDispatch.action")
	@ResponseBody
	public Map<String, Object> save(String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dispatchService.saveDispatch(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/make/deleteDispatch.action")
	@ResponseBody
	public Map<String, Object> deleteDispatch( int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dispatchService.deleteDispatch(id, caller);
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
	@RequestMapping("/pm/make/updateDispatch.action")
	@ResponseBody
	public Map<String, Object> update(String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dispatchService.updateDispatchById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/pm/make/printDispatch.action")
	@ResponseBody
	public Map<String, Object> printDispatch(int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dispatchService.printDispatch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitDispatch.action")
	@ResponseBody
	public Map<String, Object> submitDispatch(String caller, int id
			) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dispatchService.submitDispatch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitDispatch.action")
	@ResponseBody
	public Map<String, Object> resSubmitDispatch(int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dispatchService.resSubmitDispatch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditDispatch.action")
	@ResponseBody
	public Map<String, Object> auditDispatch(int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dispatchService.auditDispatch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditDispatch.action")
	@ResponseBody
	public Map<String, Object> resAuditDispatch(int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dispatchService.resAuditDispatch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/make/copyDispatch.action")
	@ResponseBody
	public Map<String, Object> copy(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", dispatchService.copyDispatch(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 通过制造单号获取detail中的字段
	 * 
	 */
	
	@RequestMapping("/pm/make/selectDispatchDetail.action")
	@ResponseBody
	public Map<String, Object> selectDetail(String makecode,Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dispatchService.selectDispatchByMakeCode(makecode,id);
		modelMap.put("success", true);
		return modelMap;
	}
}
