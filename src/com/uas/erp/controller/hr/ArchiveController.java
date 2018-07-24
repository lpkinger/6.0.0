package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.ArchiveService;

@Controller
public class ArchiveController {
	
	@Autowired
	private ArchiveService archiveService;
	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveArchive.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, 
			String param,String param2,String param3,String param4,String param5,String param6) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[]{param, param2, param3,param4,param5,param6};
		archiveService.saveArchive(formStore, params,  caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateArchive.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, 
			String param, String param2, String param3,String param4,String param5,String param6) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[]{param, param2, param3,param4,param5,param6};
		archiveService.updateArchiveById(formStore, params,  caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteArchive.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		archiveService.deleteArchive(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
