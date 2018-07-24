package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.oa.OADocumentCatalogService;

@Controller
public class OADocumentCatalogController {

	@Autowired
	private OADocumentCatalogService oaDocumentCatalogService;

	/**
	 * 保存
	 */
	@RequestMapping("/oa/saveDocumentCatalog.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String save, String update) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (save != null && !save.equals("")) {
			oaDocumentCatalogService.save(save, caller);
		}
		if (update != null && !update.equals("")) {
			oaDocumentCatalogService.update(update, caller);
		}
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/deleteDocumentCatalog.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaDocumentCatalogService.delete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
