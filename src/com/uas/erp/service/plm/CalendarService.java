package com.uas.erp.service.plm;

import com.uas.erp.model.Employee;

public interface CalendarService {
	void saveEvents(String addData, String updateData, String deleteData);

	String getCalendar(String caller, String emid, Employee employee);

	String getMyCalendar(String emcode, String condition);

	String getMyAgenda(String emid);
}
