package com.uas.erp.service.oa;

import java.util.Map;


public interface VacationService {
	
	void saveVacation(String formStore, String  caller);
	
	void updateVacationById(String formStore, String  caller);
	
	void deleteVacation(int va_id, String  caller);
	
	void auditVacation(int va_id, String  caller);
	
	void resAuditVacation(int va_id, String  caller);
	
	void submitVacation(int va_id, String  caller);
	
	void resSubmitVacation(int va_id, String  caller);
	
	void updateEmployeeHoliday(String  caller);
	
	String checkHoliday(int va_id,String  caller);
	
	void updateEmployeeHavedays(String condition);
	
	void auditAsk4Leave(int va_id, String  caller);
	
	void resAuditAsk4Leave(int va_id, String  caller);

	void confirmAsk4Leave(int id, String  caller);

	 Map<String, Object>  sickCheck(int va_id, String caller);
	 
	 void cleanEmpdays(int id, String  caller);
	 
	 void checkTime(Map<Object, Object> formStore);

	void resEndVacation(int id, String caller);

	void endVacation(int id, String caller);

	int checkDuplicateTime(String emcode,String start,String end);
}
