package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.JobDocumentPowerService;

@Controller
public class JobDocumentPowerController {
	
	@Autowired
	private JobDocumentPowerService jobDocumentPowerService;
	/*@Autowired
	private DocumentPowerService documentPowerService;*/
	/**
	 * 保存
	 */
	@RequestMapping("/hr/employee/updateJobDocumentPower.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String update) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobDocumentPowerService.update(update, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/*@RequestMapping("/hr/employee/getJobDocumentPower.action")  
	@ResponseBody 
	public Map<String, Object> get(String caller, int parentid, int joid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<DocumentPower> documentPowers = documentPowerService.getDocumentPowersByParentID(parentid);
		List<DocumentPositionPower> documentPositionPowers = new ArrayList<DocumentPositionPower>();
		modelMap.put("documentPowers", documentPowers);
		for(DocumentPower p:documentPowers){
			if(p.getDcp_isleaf().equals("T")){
				documentPositionPowers.add(jobDocumentPowerService.getDPPByDcpIdAndJoID(p.getDcp_id(), joid));
			}
		}
		modelMap.put("documentPositionPowers", documentPositionPowers);
		modelMap.put("success", true);
		return modelMap;
	}*/
}
