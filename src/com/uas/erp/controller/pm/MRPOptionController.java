package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MRPOptionService;

@Controller
public class MRPOptionController extends BaseController {
	@Autowired
	private MRPOptionService mRPOptionService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/mps/saveMRPOption.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRPOptionService.saveMRPOption(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/mps/deleteMRPOption.action")
	@ResponseBody
	public Map<String, Object> deleteMRPOption(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRPOptionService.deleteMRPOption(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/mps/updateMRPOption.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRPOptionService.updateMRPOption(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mps/submitMRPOption.action")
	@ResponseBody
	public Map<String, Object> submitMRPOption(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRPOptionService.submitMRPOption(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mps/resSubmitMRPOption.action")
	@ResponseBody
	public Map<String, Object> resSubmitMRPOption(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRPOptionService.resSubmitMRPOption(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mps/auditMRPOption.action")
	@ResponseBody
	public Map<String, Object> auditMRPOption(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRPOptionService.auditMRPOption(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mps/resAuditMRPOption.action")
	@ResponseBody
	public Map<String, Object> resAuditMRPOption(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mRPOptionService.resAuditMRPOption(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
