package com.uas.erp.service.hr;


public interface WageAttendService {

	void save(String formStore, String gridStore, String caller);

	void update(String formStore, String gridStore, String caller);

	void delete(int wa_id, String caller);

	void audit(int wa_id, String caller);

	void resAudit(int wa_id, String caller);

	void submit(int wa_id, String caller);

	void resSubmit(int wa_id, String caller);

}
