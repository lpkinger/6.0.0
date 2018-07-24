package com.uas.mobile.service;

import java.util.List;
import java.util.Map;

public interface OAMeetingService {

	Map<String, Object> getMeetingDetailParticipants(String ma_code);
	void meetingSignMobile(String em_code, String ma_code, String caller);
	void saveOutSign(String formStore, String caller);
	List<Map<String, Object>> workdata(String condition);
	Map<String,Object> saveAndSubmitAskLeave(String caller,String formStore);
	Map<String,Object> saveAndSubmitMobileSignCard(String caller,String formStore);
	Map<String,Object> commonSaveAndSubmit(String caller,String formStore,String gridStore,String emcode,String emname);
	List<Map<String,Object>> getMenuConfig(String condition);
	List<Map<String, Object>> getoaconifg();
	Map<String, Object> UpdateSubmitFYBX(String caller, String formStore,
			String param1, String param2);
	Map<String, Object> saveAndSubmitFYBX(String caller, String formStore,
			String gridStore, String gridStore2, String emcode, String emname);
}
