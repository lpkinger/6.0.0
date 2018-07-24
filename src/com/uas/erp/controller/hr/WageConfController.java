package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.hr.WageConfService;
import com.uas.erp.service.hr.WageItemService;


@Controller
public class WageConfController extends BaseController {
	@Autowired
	private WageConfService wageConfService;

	@RequestMapping("/hr/wage/conf/update.action")
	@ResponseBody
	public Map<String, Object> update(String formStore,String owgridStore,String ptgridStore,String abgridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageConfService.update(formStore,owgridStore,ptgridStore,abgridStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/hr/wage/conf/getAllConf.action")
	@ResponseBody
	public Map<String, Object> getAllConf(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("baseconf", wageConfService.getBaseConf());
		modelMap.put("owconf", wageConfService.getOverWorkConf());
		modelMap.put("abconf", wageConfService.getAbsenceConf());
		modelMap.put("ptconf", wageConfService.getPersonTaxConf());
		return modelMap;
	}

}
