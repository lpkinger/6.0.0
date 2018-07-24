package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.oa.DossierService;

@Controller
public class DossierController {
	@Autowired
	private DossierService dossierService;

	@RequestMapping("/oa/officialDocument/fileManagement/saveDossier.action")
	@ResponseBody
	public Map<String, Object> saveDossier(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dossierService.saveDossier(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/officialDocument/fileManagement/deleteDossier.action")
	@ResponseBody
	public Map<String, Object> deleteDossier(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dossierService.deleteDossier(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/officialDocument/fileManagement/updateDossier.action")
	@ResponseBody
	public Map<String, Object> updateDocumentRoom(String caller,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dossierService.updateDossier(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
