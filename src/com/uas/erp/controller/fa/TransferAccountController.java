package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.TransferAccountService;

@Controller
@RequestMapping("/fa/api")
public class TransferAccountController {

	@Autowired
	private TransferAccountService transferAccountService;
	
	/**
	 * 民生银行批量转账
	 */
	@RequestMapping("/cmbc/batchTransfer.action")
	@ResponseBody
	public Map<String, Object> batchTransfer(HttpServletRequest req,
			String data, String password) {
		String ip = req.getRemoteAddr();
		Map<String, Object> modelMap = transferAccountService.postRequests(ip, data, password, "TransferXfer", "cmbc");
		modelMap.put("result", true);
		return modelMap;
	}
	
	/**
	 * 民生银行批量查询
	 */
	@RequestMapping("/cmbc/batchSearch.action")
	@ResponseBody
	public Map<String, Object> batchSearch(HttpServletRequest req, String ids,
			String trnCode, String password) {
		String ip = req.getRemoteAddr();
		Map<String, Object> modelMap = new HashMap<String, Object>();
		transferAccountService.postRequests(ip, ids, password, "qryXfer", "cmbc");
		modelMap.put("result", true);
		return modelMap;
	}
	
	/**
	 * 浦东银行批量转账
	 */
	@RequestMapping("/spdb/batchTransfer.action")
	@ResponseBody
	public Map<String, Object> spdbTransfer(HttpServletRequest req,
			String data, String password) {
		String ip = req.getRemoteAddr();
		Map<String, Object> map = transferAccountService.postRequests(ip, data, password, "8801", "spdb");
		map.put("result", true);
		return map;
	}
	
	/**
	 * 浦东银行批量查询
	 */
	@RequestMapping("/spdb/batchSearch.action")
	@ResponseBody
	public Map<String, Object> spdbSearch(HttpServletRequest req, String ids,
			String trnCode, String password) {
		String ip = req.getRemoteAddr();
		Map<String, Object> modelMap = new HashMap<String, Object>();
		transferAccountService.postRequests(ip, ids, password, "EY03", "spdb");
		modelMap.put("result", true);
		return modelMap;
	}
	
	/**
	 * 民生银行单笔转账
	 */
/*	@RequestMapping("/cmbc/singleTransfer.action")
	@ResponseBody
	public Map<String, Object> singleTransfer(HttpServletRequest req,
			String class_, String code) {
		String ip = req.getRemoteAddr();
		Map<String, Object> flag = transferAccountService.singleTransferRequest(ip, class_, code);
		return flag;
	}*/

	/**
	 * 民生银行单笔查询
	 */
/*	@RequestMapping("/cmbc/search.action")
	@ResponseBody
	public Map<String, Object> search(HttpServletRequest req, String code,
			String class_) {
		String ip = req.getRemoteAddr();
		Map<String, Object> flag = transferAccountService.searchRequest(ip,
				class_, code);
		return flag;
	}*/
	
}
