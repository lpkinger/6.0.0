package com.uas.erp.controller.b2b;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.b2b.model.SaleReply;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.b2b.SaleDownService;

@Controller
public class SaleDownController {
	@Autowired
	private SaleDownService saleDownService;
	
	@Autowired
	private BaseDao baseDao;


	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/b2b/sale/updateSaleDown.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleDownService.updateSaleDownById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/b2b/sale/replySaleDown.action")
	@ResponseBody
	public Map<String, Object> replyAll(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleDownService.replyAll(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/b2b/sale/printSaleDown.action")
	@ResponseBody
	public Map<String, Object> printSaleDown(int id, String caller,HttpServletResponse response) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("printurl", saleDownService.printSaleDown(id, caller));
		modelMap.put("success", true);
		
		return modelMap;
	}

	/**
	 * 更改供应商回复信息
	 * */
	@RequestMapping("/b2b/sale/replyInfo.action")
	@ResponseBody
	public Map<String, Object> updateVendorBackInfo(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleDownService.updateReplyInfo(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/b2b/sale/turnSale.action")
	@ResponseBody
	public Map<String, Object> turnSale(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int said = saleDownService.turnSale(id, caller);
		modelMap.put("success", true);
		modelMap.put("id", said);
		return modelMap;
	}

	/**
	 * 查找回复记录
	 * 
	 * @param id
	 *            SaleDown ID
	 */
	@RequestMapping("/b2b/sale/getReply.action")
	@ResponseBody
	public List<SaleReply> getReply(int id) {
		return saleDownService.findReplyBySaid(id);
	}

	/**
	 * 批量回复
	 */
	@RequestMapping(value = "/b2b/sale/vastReplyInfo.action")
	@ResponseBody
	public Map<String, Object> vastReplyInfo(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = saleDownService.vastReplyInfo(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
}
