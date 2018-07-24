package com.uas.erp.controller.oa;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.oa.BookStorageService;

@Controller
public class BookStorageController extends BaseController {
	@Autowired
	private BookStorageService bookStorageService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 * @throws ParseException 
	 */
	@RequestMapping("/oa/publicAdmin/book/basicData/saveBookStorage.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookStorageService.saveBookStorage(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/oa/publicAdmin/book/basicData/deleteBookStorage.action")
	@ResponseBody
	public Map<String, Object> deleteBookStorage(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookStorageService.deleteBookStorage(id, caller);
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
	 * @throws ParseException 
	 */
	@RequestMapping("/oa/publicAdmin/book/basicData/updateBookStorage.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookStorageService.updateBookStorageById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/publicAdmin/book/basicData/submitBookStorage.action")
	@ResponseBody
	public Map<String, Object> submitBookStorage(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookStorageService.submitBookStorage(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/publicAdmin/book/basicData/resSubmitBookStorage.action")
	@ResponseBody
	public Map<String, Object> resSubmitBookStorage(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookStorageService.resSubmitBookStorage(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/publicAdmin/book/basicData/auditBookStorage.action")
	@ResponseBody
	public Map<String, Object> auditBookStorage(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookStorageService.auditBookStorage(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/publicAdmin/book/basicData/resAuditBookStorage.action")
	@ResponseBody
	public Map<String, Object> resAuditBookStorage(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookStorageService.resAuditBookStorage(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
