package com.uas.erp.service.hr;


public interface WageBaseService {

	void save(String formStore, String gridStore, String caller);

	void update(String formStore, String gridStore, String caller);

	void delete(int wb_id, String caller);

	void audit(int wb_id, String caller);

	void resAudit(int wb_id, String caller);

	void submit(int wb_id, String caller);

	void resSubmit(int wb_id, String caller);

}
