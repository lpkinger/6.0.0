package com.uas.erp.controller.wisdomPark;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.wisdomPark.AgreementService;

@Controller
public class AgreementController {
	
	
	@Autowired
	private AgreementService agreementService;
	
	@Autowired BaseDao baseDao;
	
	//保存服务协议
	@RequestMapping("/wisdomPark/saveAgreement.action")
	@ResponseBody
	public Map<String, Object> saveAgreement(String caller, String formStore){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agreementService.saveAgreement(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
		
	}
	
	//更新服务协议
	@RequestMapping("/wisdomPark/updateAgreement.action")
	@ResponseBody
	public Map<String, Object> updateAgreement(String caller, String formStore){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agreementService.updateAgreement(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//删除服务协议
	@RequestMapping("/wisdomPark/deleteAgreement.action")
	@ResponseBody
	public Map<String, Object> deleteAgreement(String caller, int id){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agreementService.deleteAgreement(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//发布服务协议
	@RequestMapping("/wisdomPark/publishAgreement.action")
	@ResponseBody
	public Map<String, Object> publishAgreement(String caller, int id){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agreementService.publishAgreement(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//撤销服务协议
	@RequestMapping("/wisdomPark/cancelAgreement.action")
	@ResponseBody
	public Map<String, Object> cancelAgreement(String caller, int id){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agreementService.cancelAgreement(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
