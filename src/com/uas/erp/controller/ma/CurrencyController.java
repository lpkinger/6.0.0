package com.uas.erp.controller.ma;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.FileUpload;
import com.uas.erp.service.ma.CurrencyService;

@Controller
public class CurrencyController {
	@Autowired
	private CurrencyService currencyService;

	@RequestMapping("ma/logic/getCurrencyDate.action")
	@ResponseBody
	public Map<String, Object> getCurrencyDate() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		currencyService.getCurrencyDate();
		modelMap.put("success", true);
		modelMap.put("data", currencyService.getCurrencyDate());
		return modelMap;
	}
	@RequestMapping("ma/logic/saveCurrency.action")
	@ResponseBody
	public Map<String, Object> saveCurrency(String formstore,String gridstore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		currencyService.saveCurrency(formstore,gridstore);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("ma/logic/getSysCurrency.action")
	@ResponseBody
	public Map<String, Object> getSysCurrency() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		currencyService.getSysCurrency();
		modelMap.put("success", true);
		modelMap.put("data", currencyService.getSysCurrency());
		return modelMap;
	}
	@RequestMapping("ma/logic/getBsCurrency.action")
	@ResponseBody
	public Map<String, Object> getBsCurrency() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		currencyService.getBsCurrency();
		modelMap.put("success", true);
		modelMap.put("data", currencyService.getBsCurrency());
		return modelMap;
	}
}
