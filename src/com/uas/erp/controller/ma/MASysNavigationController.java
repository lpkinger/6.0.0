package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.ma.MASysNavigationService;

@Controller
public class MASysNavigationController {
	
	@Autowired
	private MASysNavigationService maSysNavigationService;
	/**
	 * 根据父节点加载子节点
	 */
	@RequestMapping(value="/ma/lazyTree.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(int parentId,String condition) throws Exception{
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = maSysNavigationService.getJSONTreeByParentId(parentId,condition);
		modelMap.put("tree", tree);
		return modelMap;
	}
	/**
	 * 保存
	 */
	@RequestMapping("/ma/saveSysNavigation.action")  
	@ResponseBody 
	public Map<String, Object> save(String save, String update, String other) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if(save != null && !save.equals("")){
			maSysNavigationService.save(save);
		}
		if(update != null && !update.equals("")){
			maSysNavigationService.update(update);
		}
		if(other != null && !other.equals("")){
			maSysNavigationService.update(other);
		}
		modelMap.put("success", true);
		return modelMap;
	}
	/*
	 * 添加根节点
	 */
	@RequestMapping("/ma/addRootSysNavigation.action")  
	@ResponseBody 
	public Map<String, Object> addRoot(String save) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if(save != null && !save.equals("")){
			maSysNavigationService.addRoot(save);
		}
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	
	/**
	 * 删除
	 */
	@RequestMapping("/ma/deleteSysNavigation.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maSysNavigationService.delete(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
