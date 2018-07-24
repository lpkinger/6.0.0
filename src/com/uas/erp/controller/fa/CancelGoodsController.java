package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.CancelGoodsService;

@Controller
public class CancelGoodsController {
	@Autowired
	private CancelGoodsService cancelGoodsService;

	@RequestMapping("/fa/ars/cancelGoods.action")
	@ResponseBody
	public Map<String, Object> cancelGoods(String date) {
		cancelGoodsService.cancelGoods(Integer.parseInt(date));
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/fa/ars/cancelEstimate.action")
	@ResponseBody
	public Map<String, Object> cancelEstimate(String date) {
		cancelGoodsService.cancelEstimate(Integer.parseInt(date));
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}
}
