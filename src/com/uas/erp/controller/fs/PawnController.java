package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.PawnService;

@Controller
public class PawnController {
	@Autowired
	private PawnService pawnService;

	@RequestMapping("fs/fspledge/savePawn.action")
	@ResponseBody
	public Map<String,Object> save(String caller, String formStore,
			String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pawnService.savePawn(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/updatePawn.action")
	@ResponseBody
	public Map<String,Object> update(String caller, String formStore,
			String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pawnService.updatePawn(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/deletePawn.action")
	@ResponseBody
	public Map<String,Object> delete(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pawnService.deletePawn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/submitPawn.action")
	@ResponseBody
	public Map<String,Object> submit(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pawnService.submitPawn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/resSubmitPawn.action")
	@ResponseBody
	public Map<String,Object> resSubmit(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pawnService.resSubmitPawn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/auditPawn.action")
	@ResponseBody
	public Map<String,Object> audit(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pawnService.auditPawn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("fs/fspledge/resAuditPawn.action")
	@ResponseBody
	public Map<String,Object> resAudit(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pawnService.resAuditPawn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
