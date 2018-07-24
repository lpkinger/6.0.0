package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.DocumentSetupService;

@Controller
public class DocumentSetupController {
	@Autowired
	private DocumentSetupService documentSetupService;

	@RequestMapping("/fa/updateDocumentSetup.action")
	@ResponseBody
	public Map<String, Object> updateDocumentSetup(HttpSession session,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		documentSetupService.updateDocumentSetupById(null, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
