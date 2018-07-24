package com.uas.erp.service.oa;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.GroupTree;

public interface BatchMailService {

	/**
	 * 新建个人通讯录
	 * @param ids
	 * @param folderName
	 * @return 
	 */
	public abstract String createDir(String ids, String folderName);
	
	public Map<String, Object> getGroups();
	
	public String addToGroup(String ids, String groupName);
	
	public List<GroupTree> getGroupsTree();
	
	public List<GroupTree> getPersonByGroupName(String groupName);
	
	public List<GroupTree> addPersonToGroup(String name, String email, String group);
	
	public String updateGroupName(String name, String group);
	
	public void deleteGroup(String group);
	
	public String updatePersonInfo(String name, String email, String emId);
	
	public void deletePerson(String emId);
	
	public List<Map<String, Object>> searchReciveman(String value, String type);
	
	public Map<String, Object> send(String recivemen, String subject, String content, String files);

}