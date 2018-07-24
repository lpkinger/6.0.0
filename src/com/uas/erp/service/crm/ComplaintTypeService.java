package com.uas.erp.service.crm;



public interface ComplaintTypeService {
	void saveComplaintType(String formStore,String caller);
	
	void updateComplaintType(String formStore,String caller);
	
	void deleteComplaintType(int ct_id,String caller);
}
