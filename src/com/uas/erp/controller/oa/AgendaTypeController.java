package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.AgendaType;
import com.uas.erp.service.oa.AgendaTypeService;

@Controller
public class AgendaTypeController {
	@Autowired
	private AgendaTypeService agendaTypeService;

	/**
	 * 保存
	 */
	@RequestMapping("/oa/persontask/myAgenda/saveAgendaType.action")
	@ResponseBody
	public Map<String, Object> save(String caller, HttpServletRequest request,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agendaTypeService.saveAgendaType(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/persontask/myAgenda/updateAgendaType.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agendaTypeService.updateAgendaType(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/persontask/myAgenda/deleteAgendaType.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agendaTypeService.deleteAgendaType(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/*
	 * 日程类型列表
	 */
	@RequestMapping("/oa/persontask/myAgenda/typelist.action")
	@ResponseBody
	public Map<String, Object> listDocument(String caller, int page,
			int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<AgendaType> ats = agendaTypeService.getAll(page, pageSize);
		modelMap.put("success", ats);
		modelMap.put("count", agendaTypeService.getAllCount(caller));
		return modelMap;
	}

	/*
	 * 搜索日程类型
	 */
	@RequestMapping("/oa/persontask/myAgenda/search.action")
	@ResponseBody
	public Map<String, Object> search(String caller, String name, int page,
			int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<AgendaType> ats = agendaTypeService.searchByName(name, page,
				pageSize);
		modelMap.put("success", ats);
		modelMap.put("count", agendaTypeService.getSearchCount(name));
		return modelMap;
	}

	@RequestMapping("/oa/persontask/myAgenda/getAgendaType.action")
	@ResponseBody
	public Map<String, Object> get(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		AgendaType at = agendaTypeService.getById(id);
		modelMap.put("success", true);
		modelMap.put("color", at.getAt_color());
		return modelMap;
	}
	/**
	 * 批量删除日程类型
	 */
	@RequestMapping("/oa/persontask/myAgenda/vastDeleteAgendaType.action")
	@ResponseBody
	public Map<String, Object> vastDelete(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agendaTypeService.vastDeleteAgendaType(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
