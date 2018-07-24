package com.uas.erp.service.pm;

public interface AttendanceService {
	int copyAttendance(int id, String caller);

	void saveAttendance(String formStore, String param, String caller);

	void updateAttendance(String formStore, String param, String caller);

	void deleteAttendance(int id, String caller);

	void submitAttendance(int id, String caller);

	void resSubmitAttendance(int id, String caller);

	void auditAttendance(int id, String caller);

	void resAuditAttendance(int id, String caller);
}
