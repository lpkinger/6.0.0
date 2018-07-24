package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.GroupTree;
import com.uas.erp.service.oa.BatchMailService;

@Controller
public class BacthMailController {
	
	@Autowired
	private BatchMailService batchMailService;

	@RequestMapping("/oa/bacthmail/getHrOrgTree.action")
	@ResponseBody
	public Map<String, Object> getHrOrgTreeByParentId(int parentId) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
//		List<CheckTree> tree = hrOrgService.getHrOrgTree(caller);
//		modelMap.put("tree", tree);
		return modelMap;
	}
	
	@RequestMapping("/oa/batchmail/createDir.action")
	@ResponseBody
	public Map<String, Object> createDir(String ids, String folderName){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String msg = batchMailService.createDir(ids, folderName);
		modelMap.put("msg", msg);
		return modelMap;
	}
	
	@RequestMapping("/oa/batchmail/getGroups.action")
	@ResponseBody
	public Map<String, Object> getGroups(){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String, Object> groupsMap = batchMailService.getGroups();
		String groups = String.valueOf(groupsMap.get("groups"));
		modelMap.put("groups", groups);
		return modelMap;
	}
	
	@RequestMapping("/oa/batchmail/addToGroup.action")
	@ResponseBody
	public Map<String, Object> addToGroup(String ids, String groupName){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String msg = batchMailService.addToGroup(ids, groupName);
		modelMap.put("msg", msg);
		return modelMap;
	}
	
	@RequestMapping("/oa/batchmail/getGroupsTree")
	@ResponseBody
	public Map<String, Object> getGroupsTree(){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<GroupTree> tree = batchMailService.getGroupsTree();
		modelMap.put("tree", tree);
		return modelMap;
	}
	
	@RequestMapping("/oa/batchmail/getPersonByGroupName.action")
	@ResponseBody
	public Map<String, Object> getPersonByGroupName(String groupName){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<GroupTree> tree = batchMailService.getPersonByGroupName(groupName);
		modelMap.put("tree", tree);
		return modelMap;
	}
	
	@RequestMapping("/oa/batchmail/addPersonToGroup.action")
	@ResponseBody
	public Map<String, Object> addPersonToGroup(String name, String email, String group){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<GroupTree> tree = batchMailService.addPersonToGroup(name, email, group);
		modelMap.put("msg", "添加成功!");
		modelMap.put("tree",tree);
		return modelMap;
	}
	
	@RequestMapping("/oa/batchmail/updateGroupName.action")
	@ResponseBody
	public Map<String, Object> updateGroupName(String name, String group){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String msg = batchMailService.updateGroupName(name, group);
		modelMap.put("msg", msg);
		return modelMap;
	}
	
	@RequestMapping("/oa/batchmail/deleteGroup.action")
	@ResponseBody
	public Map<String, Object> deleteGroup(String group){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchMailService.deleteGroup(group);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/oa/batchmail/updatePersonInfo.action")
	@ResponseBody
	public Map<String, Object> updatePersonInfo(String name, String email, String cgId){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String msg = batchMailService.updatePersonInfo(name, email, cgId);
		modelMap.put("msg", msg);
		return modelMap;
	}
	
	@RequestMapping("/oa/batchmail/deletePerson.action")
	@ResponseBody
	public Map<String, Object> deletePerson(String cgId){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchMailService.deletePerson(cgId);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/oa/batchMail/searchReciveman.action")
	@ResponseBody
	public Map<String, Object> searchReciveman(String value, String type){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String,Object>> result = batchMailService.searchReciveman(value, type);
		modelMap.put("result", result);
		return modelMap;
	}
	
	@RequestMapping("/oa/batchMail/send.action")
	@ResponseBody
	public Map<String, Object> send(String recivemen, String subject, String content, String files){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = batchMailService.send(recivemen,subject,content,files);
		return modelMap;
	}
	
}
