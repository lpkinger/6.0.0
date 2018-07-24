package com.uas.erp.service.hr;

public interface AttendanceManageService {
	void result(String startdate,String enddate,boolean toAttendanceConfirm);

	void AttendConfirm(String caller, int id);

	void AttendResConfirm(String caller, int id);
}
