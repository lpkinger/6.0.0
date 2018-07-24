package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.PreapBillService;

@Controller
public class PreapBillController {
	@Autowired
	private PreapBillService preapBillService;

	/**
	 * 转借货出库
	 */
	@RequestMapping("/fa/ars/PreapBill/turn.action")
	@ResponseBody
	public Map<String, Object> turnBorrow(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		int ab_id = preapBillService.turn(id, caller);
		modelMap.put("success", true);
		modelMap.put("id", ab_id);
		return modelMap;
	}
}
