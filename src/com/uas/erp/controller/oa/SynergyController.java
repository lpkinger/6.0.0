package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Synergy;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.oa.SynergyService;

@Controller
public class SynergyController {
	@Autowired
	private SynergyService synergyService;
	@Autowired
	private FilePathService filePathService;
	/**
	 * 保存
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/oa/myProcess/synergy/saveSynergy.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		System.out.println(formStore);
		synergyService.saveSynergy(formStore,  caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/oa/myProcess/synergy/updateSynergy.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		synergyService.updateSynergy(formStore,  caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/oa/myProcess/synergy/deleteSynergy.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		synergyService.deleteSynergy(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/oa/myProcess/synergy/submitSynergy.action")  
	@ResponseBody 
	public Map<String, Object> submit(String caller, int id) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		synergyService.submitSynergy(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/oa/myProcess/synergy/delete.action")  
	@ResponseBody 
	public Map<String, Object> delete2(String caller, String ids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] aid = ids.split(",");
		for (String  id : aid) {
			synergyService.deleteById(Integer.parseInt(id));			
		}
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/oa/myProcess/synergy/getSynergy.action")  
	@ResponseBody 
	public Map<String, Object> get(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Synergy sy = synergyService.getSynergyById(id);
		modelMap.put("success", true);
		modelMap.put("synergy",sy);
		return modelMap;
	}
	
	@RequestMapping("/oa/myProcess/synergy/getSynergyList.action")  
	@ResponseBody 
	public Map<String, Object> getAll(String caller, int page, int pageSize) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Synergy> list = synergyService.getList(page, pageSize);
		modelMap.put("success", list);
		modelMap.put("count", synergyService.getListCount());
		return modelMap;
	}
	
	@RequestMapping("/oa/myProcess/synergy/search.action")  
	@ResponseBody 
	public Map<String, Object> search(String caller, int page, int pageSize, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Synergy> list = synergyService.getByCondition(condition, page, pageSize);
		modelMap.put("success", list);
		modelMap.put("count", synergyService.getSearchCount(condition));
		return modelMap;
	}
	@RequestMapping("/oa/myProcess/synergy/getAttach.action")  
	@ResponseBody 
	public Map<String, Object> getAttach(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("path", filePathService.getFilepath(id));
		return modelMap;
	}
	
}
