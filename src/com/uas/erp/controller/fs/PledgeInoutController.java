package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.PledgeInoutService;

@Controller
public class PledgeInoutController {
	@Autowired
	private PledgeInoutService PledgeInoutService;

	@RequestMapping("fs/fspledge/savePledgeInout.action")
	@ResponseBody
	public Map<String,Object> save(String caller, String formStore,
			String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PledgeInoutService.savePledgeInout(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/updatePledgeInout.action")
	@ResponseBody
	public Map<String,Object> update(String caller, String formStore,
			String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PledgeInoutService.updatePledgeInout(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/deletePledgeInout.action")
	@ResponseBody
	public Map<String,Object> delete(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PledgeInoutService.deletePledgeInout(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/submitPledgeInout.action")
	@ResponseBody
	public Map<String,Object> submit(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PledgeInoutService.submitPledgeInout(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/resSubmitPledgeInout.action")
	@ResponseBody
	public Map<String,Object> resSubmit(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PledgeInoutService.resSubmitPledgeInout(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/auditPledgeInout.action")
	@ResponseBody
	public Map<String,Object> audit(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PledgeInoutService.auditPledgeInout(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/resAuditPledgeInout.action")
	@ResponseBody
	public Map<String,Object> resAudit(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PledgeInoutService.resAuditPledgeInout(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
