package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.BarCodeService;

@Controller
public class BarCodeController {

	@Autowired
	private BarCodeService barCodeService;

	/**
	 * 获取条码
	 */
	@RequestMapping(value = "/scm/pm/mes/getBar.action")
	@ResponseBody
	public Map<String, Object> getBar(String codes) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", barCodeService.getBar(codes));
		modelMap.put("success", true);
		return modelMap;
	}

}
