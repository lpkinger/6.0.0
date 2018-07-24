package com.uas.erp.service.crm;



public interface TaskTemplatesService {
	void saveTaskTemplates(String formStore,String gridStore,String caller);
	void deleteTaskTemplates(int tt_id,String caller);
	void updateTaskTemplates(String formStore,String gridStore,String caller);
}
