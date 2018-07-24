package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.PledgeService;

@Controller
public class PledgeController {
	@Autowired
	private PledgeService pledgeService;

	@RequestMapping("fs/fspledge/savePledge.action")
	@ResponseBody
	public Map<String,Object> save(String caller, String formStore,
			String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pledgeService.savePledge(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/updatePledge.action")
	@ResponseBody
	public Map<String,Object> update(String caller, String formStore,
			String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pledgeService.updatePledge(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/deletePledge.action")
	@ResponseBody
	public Map<String,Object> delete(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pledgeService.deletePledge(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/submitPledge.action")
	@ResponseBody
	public Map<String,Object> submit(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pledgeService.submitPledge(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/resSubmitPledge.action")
	@ResponseBody
	public Map<String,Object> resSubmit(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pledgeService.resSubmitPledge(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/auditPledge.action")
	@ResponseBody
	public Map<String,Object> audit(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pledgeService.auditPledge(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/resAuditPledge.action")
	@ResponseBody
	public Map<String,Object> resAudit(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pledgeService.resAuditPledge(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
