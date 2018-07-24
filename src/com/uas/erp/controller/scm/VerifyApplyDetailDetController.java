package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.VerifyApplyDetailDetService;

@Controller
public class VerifyApplyDetailDetController {

	@Autowired
	private VerifyApplyDetailDetService verifyApplyDetailDetService;

	@RequestMapping("scm/qc/saveVerifyApplyDetailDet.action")
	@ResponseBody
	public Map<String, Object> updateAccountVerifyApplyDetailDet(String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyDetailDetService.saveVerifyApplyDetailDetById(null, param);
		modelMap.put("success", true);
		return modelMap;
	}
}
