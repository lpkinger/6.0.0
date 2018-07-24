package com.uas.sysmng.service;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.MessageRole;


public interface SysmngMessageService {

	Map<String, Object> getMessageFormData( String id);
	List<MessageRole> getMessageGridData( String id);
	Boolean deleteData(String id);
	Boolean toolbarDelete(String id);
	
	Boolean updateData(String formData,String gridData1,String gridData2);
	Map<String, Object> saveData(String formData,String gridData);
	
	
}
