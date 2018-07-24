package com.uas.erp.controller.oa;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.oa.BorrowAppService;

@Controller
public class BorrowAppController extends BaseController {
	@Autowired
	private BorrowAppService borrowAppservice;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 * @throws ParseException 
	 */
	@RequestMapping("/oa/publicAdmin/book/borrowManage/saveBorrowList.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowAppservice.saveBorrowList(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/oa/publicAdmin/book/borrowManage/deleteBorrowList.action")
	@ResponseBody
	public Map<String, Object> deleteBorrowList(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowAppservice.deleteBorrowList(id, caller);
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
	@RequestMapping("/oa/publicAdmin/book/borrowManage/updateBorrowList.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowAppservice.updateBorrowListById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/publicAdmin/book/borrowManage/submitBorrowList.action")
	@ResponseBody
	public Map<String, Object> submitBorrowList(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowAppservice.submitBorrowList(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/publicAdmin/book/borrowManage/resSubmitBorrowList.action")
	@ResponseBody
	public Map<String, Object> resSubmitBorrowList(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowAppservice.resSubmitBorrowList(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/publicAdmin/book/borrowManage/auditBorrowList.action")
	@ResponseBody
	public Map<String, Object> auditBorrowList(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowAppservice.auditBorrowList(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/publicAdmin/book/borrowManage/resAuditBorrowList.action")
	@ResponseBody
	public Map<String, Object> resAuditBorrowList(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowAppservice.resAuditBorrowList(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 归还按钮
	 */
	@RequestMapping(value = "/oa/publicAdmin/book/returnManage/vastReturn.action")
	@ResponseBody
	public Map<String, Object> vastClose(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log=borrowAppservice.vastReturn(caller, data);
		modelMap.put("log",log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 续借按钮
	 */
	@RequestMapping(value = "/oa/publicAdmin/book/returnManage/vastRenew.action")
	@ResponseBody
	public Map<String, Object> vastRenew(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowAppservice.vastRenew(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 催还
	 */
	@RequestMapping(value = "/oa/publicAdmin/book/returnManage/OverDue.action")
	@ResponseBody
	public Map<String, Object> OverDue(String caller,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log=borrowAppservice.OverDue(caller, data);
		modelMap.put("log",log);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/oa/publicAdmin/book/borrowManage/getBorrowListModule.action")
	@ResponseBody
	public Map<String, Object> getBorrowListModule(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = borrowAppservice.getJSONModule(caller);
		modelMap.put("tree", tree);
		modelMap.put("success", true);
		return modelMap;
	}
}
