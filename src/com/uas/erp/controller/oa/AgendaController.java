package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Agenda;
import com.uas.erp.service.oa.AgendaService;


@Controller
public class AgendaController {
	@Autowired
	private AgendaService agendaService;
	/*
	 * 日程类型列表
	 */
	@RequestMapping("/oa/persontask/myAgenda/myArrangeList.action")  
	@ResponseBody 
	public Map<String, Object> myArrange(String caller, int page, int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Agenda> ags = agendaService.getArrangeList(caller, page, pageSize);
		modelMap.put("success", ags);
		modelMap.put("count",agendaService.getArrangeListCount(caller));
		return modelMap;
	}
	/*
	 * 日程类型列表
	 */
	@RequestMapping("/oa/persontask/myAgenda/listAgenda.action")  
	@ResponseBody 
	public Map<String, Object> listAgenda(String caller, int page, int pageSize) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Agenda> ags = agendaService.getList(caller, page, pageSize);
		modelMap.put("success", ags);
		modelMap.put("count",agendaService.getListCount(caller));
		return modelMap;
	}
	/*
	 * 删除日程类型
	 */
	@RequestMapping("/oa/persontask/myAgenda/deleteArrange.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, String ids) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] aid = ids.split(",");
		for (String  id : aid) {
			agendaService.deleteById(Integer.parseInt(id));			
		}
		modelMap.put("success", true);
		return modelMap;
	}
	/*
	 * 搜索日程类型
	 */
	@RequestMapping("/oa/persontask/myAgenda/searchArrange.action")  
	@ResponseBody 
	public Map<String, Object> search(String caller, String condition, int page, int pageSize) {	
		System.out.println(condition);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Agenda> ags = agendaService.searchByCondition(condition, page, pageSize);
		modelMap.put("success", ags);
		modelMap.put("count",agendaService.getSearchCount(condition));
		return modelMap;
	}
	/*
	 * 搜索日程类型
	 */
	@RequestMapping("/oa/persontask/myAgenda/getAgenda.action")  
	@ResponseBody 
	public Map<String, Object> search(String caller, int id) {
	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Agenda ag = agendaService.getAgendaById(id);
		modelMap.put("success", true);
		modelMap.put("agenda",ag);
		return modelMap;
	}

}
