package com.uas.erp.service.hr;


public interface AttendItemService {
	
	void saveAttendItem(String formStore, String  caller);
	
	void updateAttendItemById(String formStore, String  caller);
	
	void deleteAttendItem(int ai_id, String  caller);//attendDataCom
	
	void attendDataCom(String emcode, String startdate,String enddate, String  caller);
	
	void cardLogImp(String cardcode, String startdate,String enddate,String yearmonth, String  caller);
}
