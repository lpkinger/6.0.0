package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.drools.lang.dsl.DSLMapParser.mapping_file_return;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.BookService;

@Controller
public class BookController {
	@Autowired
	private BookService bookService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/publicAdmin/book/bookManage/saveBook.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookService.saveBook(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/oa/publicAdmin/book/bookManage/deleteBook.action")
	@ResponseBody
	public Map<String, Object> deleteBook(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookService.deleteBook(id, caller);
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
	@RequestMapping("/oa/publicAdmin/book/bookManage/updateBook.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookService.updateBookdById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/publicAdmin/book/bookManage/submitBook.action")
	@ResponseBody
	public Map<String, Object> submitBook(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookService.submitBook(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/publicAdmin/book/bookManage/resSubmitBook.action")
	@ResponseBody
	public Map<String, Object> resSubmitBook(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookService.resSubmitBook(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/publicAdmin/book/bookManage/auditBook.action")
	@ResponseBody
	public Map<String, Object> auditBook(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookService.auditBook(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/publicAdmin/book/bookManage/resAuditBook.action")
	@ResponseBody
	public Map<String, Object> resAuditBook(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookService.resAuditBook(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/*
	 * 批量转禁用
	 */
	@RequestMapping("/oa/publicAdmin/book/bookManage/turnBanned.action")
	@ResponseBody
	public Map<String, Object> vastTurnBanned(String caller, String data){
		Map<String, Object> modelMap=new HashMap<String, Object>();
		String log=bookService.turnBanned(caller,data);
		modelMap.put("log",log);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
