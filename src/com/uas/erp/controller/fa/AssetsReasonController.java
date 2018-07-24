package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.AssetsReasonService;

@Controller
public class AssetsReasonController extends BaseController {
	@Autowired
	private AssetsReasonService assetsReasonService;

	@RequestMapping("/fa/fix/saveAssetsReason.action")
	@ResponseBody
	public Map<String, Object> saveAssetsReason(HttpSession session,
			String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsReasonService.saveAssetsReason(caller, param);
		modelMap.put("success", true);
		return modelMap;
	}

}
