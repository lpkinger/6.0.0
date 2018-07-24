package com.uas.sysmng.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.MessageRole;
import com.uas.erp.model.SubsFormulaDet;
import com.uas.sysmng.service.SysmngMessageService;


@Controller
@RequestMapping("/sysmng")
public class SysmngMessageController {
	
	@Autowired
	private SysmngMessageService sysmngMessageService ;
	@Autowired
	private BaseDao baseDao;
	
	@RequestMapping(value = "/getMessageFormData.action")
	@ResponseBody
	public Map<String, Object> getMessageFormData(HttpServletRequest request,String id) {
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String, Object> modelMap =sysmngMessageService.getMessageFormData(id);
		map.put("data", modelMap);
		return map;
	}
	@RequestMapping(value = "/getMessageGridData.action")
	@ResponseBody
	public Map<String, Object> getMessageGridData(HttpServletRequest request,String id) {
		Map<String,Object> map = new HashMap<String,Object>();
		List<MessageRole>  modelMap =sysmngMessageService.getMessageGridData(id);
		map.put("data", modelMap);
		return map;
	}
	@RequestMapping(value = "/saveData.action")
	@ResponseBody
	public Map<String, Object> saveData(HttpServletRequest request,String formData,String gridData) {
		Map<String,Object> map = new HashMap<String,Object>();
		map =sysmngMessageService.saveData(formData,gridData);
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/deleteData.action")
	@ResponseBody
	public Map<String, Object> deleteData(HttpServletRequest request,String id) {
		Map<String,Object> map = new HashMap<String,Object>();
		Boolean modelMap =sysmngMessageService.deleteData(id);
		map.put("success", modelMap);
		return map;
	}
	
	@RequestMapping(value = "/toolbarDelete.action")
	@ResponseBody
	public Map<String, Object> toolbarDelete(HttpServletRequest request,String id) {
		Map<String,Object> map = new HashMap<String,Object>();
		Boolean modelMap =sysmngMessageService.toolbarDelete(id);
		map.put("success", modelMap);
		return map;
	}
	
	@RequestMapping(value = "/updateData.action")
	@ResponseBody
	public Map<String, Object> updateData(HttpServletRequest request,String formData,String gridData1,String gridData2) {
		Map<String,Object> map = new HashMap<String,Object>();
		Boolean modelMap =sysmngMessageService.updateData(formData,gridData1,gridData2);
		map.put("success", modelMap);
		return map;
	}
	
	
	
	





	
	

}
