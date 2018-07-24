package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.BookAirTicketService;

@Controller
public class BookAirTicketController {

	@Autowired
	private BookAirTicketService bookAirTicketService;

	/**
	 * 审核
	 */
	@RequestMapping("/oa/check/auditBookAirTicket.action")
	@ResponseBody
	public Map<String, Object> auditBookAirTicket(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookAirTicketService.auditBookAirTicket(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核出差申请流程处理
	 */
	@RequestMapping("/oa/check/auditFeePleaseCCSQ.action")
	@ResponseBody
	public Map<String, Object> auditFeePleaseCCSQ(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookAirTicketService.auditFeePleaseCCSQ(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
