package com.uas.mobile.service;

import java.util.Map;

import javax.servlet.http.HttpSession;

import com.uas.mobile.model.Panel;

public interface PanelService {
	 Panel getPanelByCaller(String caller, String formCondition,String gridCondition,String emcode);
	 Map<String,Object> getProductDetail(String code);
	 public Map<String,Object> getFormAndGridDetail(String caller,String condition,String isprocess,String config,HttpSession session);
	 public void updateDetailData(String formStore, String gridStore);
	 void updateMobileDefault(String caller,String formStore);
	 void updateMobileused(String caller,String formStore);
	 void deleteMobileFields(String caller,String fields);
	 
	 public Map<String,Object> getFormPanel (HttpSession session,String caller);
	 
	 Map<String,Object> getGridPanel (String  caller,String condition);
	 
	 Map<String,Object> getFormPanelAndData(HttpSession session,String caller,Integer id,String condition);
	 
	 Map<String,Object>getGridPanelandDataPage(String caller,String condition,int page,int pageSize);
	 
	 Map<String,Object> getFormConfig(String caller);
}
