package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.RelatedPartyService;

@Controller
public class RelatedPartyController extends BaseController {
	
	@Autowired
	private RelatedPartyService relatedPartyService;

	
	/**
	 * 更新
	 */
	@RequestMapping("/fa/gla/updateRelatedParty.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String sets) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		relatedPartyService.updateRelatedParty(caller, sets);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 刷新关联方
	 */
	@RequestMapping("/fa/gla/refreshRelatedParty.action")
	@ResponseBody
	public Map<String, Object> refresh(String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		relatedPartyService.refreshRelatedParty(caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
