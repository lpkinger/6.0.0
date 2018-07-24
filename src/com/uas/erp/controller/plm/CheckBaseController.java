package com.uas.erp.controller.plm;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.CheckBaseService;
@Controller
public class CheckBaseController extends BaseController{
	@Autowired
	private CheckBaseService checkBaseService;
	@RequestMapping(value="/plm/check/updateCheckBase.action")
	@ResponseBody
	public Map<String, Object> submitCheck(String formStore){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkBaseService.saveCheckBase(formStore);
		modelMap.put("success",true);
		return modelMap;
	}
	@RequestMapping(value="/plm/check/resSubmitCheckBase.action")
	@ResponseBody
	public Map<String, Object> submitCheck(int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkBaseService.resSubmitCheckBase(id);
		modelMap.put("success",true);
		return modelMap;
	}
	@RequestMapping(value="/plm/check/checkBaseToBug.action")
	@ResponseBody
	public Map<String, Object> checkBaseToBug(String formStore){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkBaseService.checkBaseToBug(formStore);
		modelMap.put("success",true);
		return modelMap;
	}
}
