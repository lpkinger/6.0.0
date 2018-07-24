package com.uas.erp.service.crm;



public interface AssistRequireDetailService {
	void saveAssistRequireDetail(String formStore,String caller);
	void deleteAssistRequireDetail(int ard_id,String caller);
	void updateAssistRequireDetail(String formStore,String caller);
}
