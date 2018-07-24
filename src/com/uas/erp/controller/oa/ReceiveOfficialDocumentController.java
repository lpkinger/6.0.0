package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.ReceiveOfficialDocument;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.oa.ReceiveODMService;

@Controller
public class ReceiveOfficialDocumentController {
	@Autowired
	private ReceiveODMService receiveODMService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private FilePathService filePathService;
	
	@RequestMapping(value="/oa/officialDocument/receiveODManagement/saveRegister.action")
	@ResponseBody
	public Map<String, Object> submitRODRegister(String caller,String formStore){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		receiveODMService.saveROD(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/oa/officialDocument/receiveODManagement/deleteRegister.action")  
	@ResponseBody 
	public Map<String, Object> deleteROD(String caller, int id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		receiveODMService.deleteROD(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/oa/officialDocument/receiveODManagement/updateRegister.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		receiveODMService.updateRODById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/oa/officialDocument/receiveODManagement/submitROD.action")  
	@ResponseBody 
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		receiveODMService.submitROD(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/oa/officialDocument/getRODDetail.action")  
	@ResponseBody 
	public Map<String, Object> getRODDetail(String caller, int id) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceiveOfficialDocument rod = receiveODMService.getRODById(id, caller);
		modelMap.put("rod", rod);
		String registrant = employeeService.getEmployeeById(rod.getRod_registrant_id()).getEm_name();
		modelMap.put("rod_registrant", registrant);
		String attach = "";
		if (rod.getRod_attach() != null && rod.getRod_attach() != "") {
			String[] fpid = rod.getRod_attach().split(";");
			for (String st : fpid) {
				if(st != "" && st != null){
					attach += filePathService.getFilepath(Integer.parseInt(st))+";";					
				}
			}
			attach = attach.substring(0, attach.lastIndexOf(";"));
		}
		modelMap.put("rod_attach", attach);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/oa/officialDocument/getRODDetail2.action")  
	@ResponseBody 
	public Map<String, Object> getRODDetail2(String caller, int id) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceiveOfficialDocument rod = receiveODMService.getRODById(id, caller);
		modelMap.put("rod", rod);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/*============================================================================================*/
	@RequestMapping("/oa/officialDocument/receiveODM/deleteROD.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, String ids) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] aid = ids.split(",");
		for (String  id : aid) {
			receiveODMService.deleteById(Integer.parseInt(id));			
		}
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/oa/officialDocument/receiveODM/getROD.action")  
	@ResponseBody 
	public Map<String, Object> get(String caller, int id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceiveOfficialDocument rod = receiveODMService.getRODById(id,caller);
		modelMap.put("success", true);
		modelMap.put("rod",rod);
		return modelMap;
	}
	
	@RequestMapping("/oa/officialDocument/receiveODM/getRODList.action")  
	@ResponseBody 
	public Map<String, Object> getAll(String caller, int page, int pageSize) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<ReceiveOfficialDocument> list = receiveODMService.getList(page, pageSize);
		modelMap.put("success", list);
		modelMap.put("count", receiveODMService.getListCount());
		return modelMap;
	}
	
	@RequestMapping("/oa/officialDocument/receiveODM/search.action")  
	@ResponseBody 
	public Map<String, Object> search(String caller, int page, int pageSize, String condition) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<ReceiveOfficialDocument> list = receiveODMService.getByCondition(condition, page, pageSize);
		modelMap.put("success", list);
		modelMap.put("count", receiveODMService.getSearchCount(condition));
		return modelMap;
	}
	@RequestMapping("/oa/officialDocument/receiveODM/getAttach.action")  
	@ResponseBody 
	public Map<String, Object> getAttach(String caller, int id) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("path", filePathService.getFilepath(id));
		return modelMap;
	}

}
