package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Instruction;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.oa.InstructionService;

@Controller
public class InstructionController {
	@Autowired
	private InstructionService instructionService;
	@Autowired
	private FilePathService filePathService;
	
	@RequestMapping(value="/oa/officialDocument/instruction/save.action")
	@ResponseBody
	public Map<String, Object> saveInstruction(String caller,String formStore){
		System.out.println(formStore);		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		instructionService.saveInstruction(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/oa/officialDocument/instruction/delete.action")  
	@ResponseBody 
	public Map<String, Object> deleteInstruction(String caller, int id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		instructionService.deleteInstruction(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/oa/officialDocument/instruction/update.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		instructionService.updateInstructionById(formStore,  caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/oa/officialDocument/instruction/submitInstruction.action")  
	@ResponseBody 
	public Map<String, Object> submit(String caller, int id) {
	Map<String, Object> modelMap = new HashMap<String, Object>();
		instructionService.submitInstruction(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/oa/officialDocument/getInstructionDetail.action")  
	@ResponseBody 
	public Map<String, Object> getRODDetail(String caller, int id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Instruction instruction = instructionService.getInstructionById(id,  caller);
		modelMap.put("instruction", instruction);
		String attach = "";
		if (instruction.getIn_attach() != null && instruction.getIn_attach() != "") {
			String[] fpid = instruction.getIn_attach().split(",");
			for (String st : fpid) {
				attach += filePathService.getFilepath(Integer.parseInt(st))+";";
			}
			attach = attach.substring(0, attach.lastIndexOf(";"));
		}
		modelMap.put("in_attach", attach);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/oa/officialDocument/getInstructionDetail2.action")  
	@ResponseBody 
	public Map<String, Object> getRODDetail2(String caller, int id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Instruction instruction = instructionService.getInstructionById(id,  caller);
		modelMap.put("instruction", instruction);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/*============================================================================================*/
	@RequestMapping("/oa/officialDocument/instruction/deleteInstruction.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, String ids) {
	Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] aid = ids.split(",");
		for (String  id : aid) {
			instructionService.deleteById(Integer.parseInt(id));			
		}
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/oa/officialDocument/instruction/getInstruction.action")  
	@ResponseBody 
	public Map<String, Object> get(String caller, int id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Instruction instruction = instructionService.getInstructionById(id, caller);
		modelMap.put("success", true);
		modelMap.put("instruction",instruction);
		return modelMap;
	}
	
	@RequestMapping("/oa/officialDocument/instruction/getInstructionList.action")  
	@ResponseBody 
	public Map<String, Object> getAll(String caller, int page, int pageSize) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Instruction> list = instructionService.getList(page, pageSize);
		modelMap.put("success", list);
		modelMap.put("count", instructionService.getListCount());
		return modelMap;
	}
	
	@RequestMapping("/oa/officialDocument/instruction/search.action")  
	@ResponseBody 
	public Map<String, Object> search(String caller, int page, int pageSize, String condition) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Instruction> list = instructionService.getByCondition(condition, page, pageSize);
		modelMap.put("success", list);
		modelMap.put("count", instructionService.getSearchCount(condition));
		return modelMap;
	}
	@RequestMapping("/oa/officialDocument/instruction/getAttach.action")  
	@ResponseBody 
	public Map<String, Object> getAttach(String caller, int id) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("path", filePathService.getFilepath(id));
		return modelMap;
	}

}
