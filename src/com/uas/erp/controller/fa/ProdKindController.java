package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.ProdKindService;

@Controller("prodkind")
public class ProdKindController {
	@Autowired
	private ProdKindService prodKindService;

	@RequestMapping("/fa/ars/updateProdKind.action")
	@ResponseBody
	public Map<String, Object> updateSaleKind(HttpSession session,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodKindService.updateProdKindById(null, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
