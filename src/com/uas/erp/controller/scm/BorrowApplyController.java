package com.uas.erp.controller.scm;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.BorrowApplyService;

@Controller
public class BorrowApplyController extends BaseController {
	@Autowired
	private BorrowApplyService borrowApplyService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveBorrowApply.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowApplyService.saveBorrowApply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteBorrowApply.action")  
	@ResponseBody 
	public Map<String, Object> deleteBorrowApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowApplyService.deleteBorrowApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateBorrowApply.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowApplyService.updateBorrowApplyById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printBorrowApply.action")  
	@ResponseBody 
	public Map<String, Object> printBorrowApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowApplyService.printBorrowApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitBorrowApply.action")  
	@ResponseBody 
	public Map<String, Object> submitBorrowApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowApplyService.submitBorrowApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitBorrowApply.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitBorrowApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowApplyService.resSubmitBorrowApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditBorrowApply.action")  
	@ResponseBody 
	public Map<String, Object> auditBorrowApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowApplyService.auditBorrowApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditBorrowApply.action")  
	@ResponseBody 
	public Map<String, Object> resAuditBorrowApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		borrowApplyService.resAuditBorrowApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转借货出库
	 */
	@RequestMapping("/scm/sale/turnBorrow.action")  
	@ResponseBody 
	public Map<String, Object> turnBorrow(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int pi_id=borrowApplyService.turnBorrow(id, caller);
		modelMap.put("success", true);
		modelMap.put("id", pi_id);
		return modelMap;
	}
	/**
	 * 借货申请单转借货出货单(申请单界面)
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/scm/sale/turnProdBorrow.action")
	@ResponseBody
	public Map<String, Object> turnProdBorrow(String data, String caller)
			throws UnsupportedEncodingException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = borrowApplyService.turnProdBorrow(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
}
