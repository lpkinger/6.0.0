package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.ma.MADBfindSetService;
import com.uas.erp.service.ma.MADBfindSetUIService;
import com.uas.erp.service.ma.MADataDictionaryService;
import com.uas.erp.service.ma.MADataListService;
import com.uas.erp.service.ma.MADocumentSetupService;
import com.uas.erp.service.ma.MAFormService;

@Controller
public class PageSetController {
	@Autowired
	private MADataDictionaryService maDataDictionaryService;
	@Autowired
	private MADBfindSetUIService madBfindSetUIService;
	@Autowired
	private MAFormService maFormService;
	@Autowired
	private MADocumentSetupService maDocumentSetupService;
	@Autowired
	private MADBfindSetService madBfindSetService;
	@Autowired
	private MADataListService maDataListService;
	/**
	 * 根据caller核查各项配置
	 */
	@RequestMapping("/common/getPageSet.action")  
	@ResponseBody 
	public Map<String, Object> getPageSet(HttpSession session, String fo_caller, String fo_table) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if(maDataDictionaryService.checkTable(fo_table)){//table在dataDictionary不存在(count==0)
			modelMap.put("dataDictionary", false);
		} else {
			modelMap.put("dataDictionary", true);
		}
		if(madBfindSetUIService.checkByCaller(fo_caller)){
			modelMap.put("dbfindSetUI", false);
		} else {
			modelMap.put("dbfindSetUI", true);
		}
		if(maFormService.checkCaller(fo_caller)){
			modelMap.put("form", false);
		} else {
			modelMap.put("form", true);
		}
		if(maDocumentSetupService.checkCaller(fo_caller)){
			modelMap.put("documentSetup", false);
		} else {
			modelMap.put("documentSetup", true);
		}
		if(madBfindSetService.checkCaller(fo_caller)){
			modelMap.put("dbfindSet", false);
		} else {
			modelMap.put("dbfindSet", true);
		}
		if(madBfindSetService.checkCaller(fo_caller)){
			modelMap.put("dbfindSet", false);
		} else {
			modelMap.put("dbfindSet", true);
		}
		if(maDataListService.checkCaller(fo_caller)){
			modelMap.put("datalist", false);
		} else {
			modelMap.put("datalist", true);
		}
		modelMap.put("success", true);
		return modelMap;
	}
}
