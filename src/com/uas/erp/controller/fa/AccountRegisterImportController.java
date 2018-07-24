package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.AccountRegisterImportService;

@Controller
public class AccountRegisterImportController {
	@Autowired
	private AccountRegisterImportService accountRegisterImportService;

	/**
	 * 清除明细
	 */
	@RequestMapping("/fa/gs/cleanAccountRegisterImport.action")
	@ResponseBody
	public Map<String, Object> cleanAccountRegisterImport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterImportService.cleanAccountRegisterImport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 清除失败的数据
	 */
	@RequestMapping("/fa/gs/cleanFailed.action")
	@ResponseBody
	public Map<String, Object> cleanFailed(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterImportService.cleanFailed(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/gs/deleteAccountRegisterImport.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterImportService.delete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存
	 */
	@RequestMapping("/fa/gs/saveAccountRegisterImport.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterImportService.saveAccountRegisterImportById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/gs/updateAccountRegisterImport.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterImportService.updateAccountRegisterImportById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量导入
	 */
	@RequestMapping("/fa/gs/accountRegisterImport.action")
	@ResponseBody
	public Map<String, Object> accountRegisterImport(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterImportService.accountRegisterImport(id);
		modelMap.put("success", true);
		return modelMap;
	}

}
