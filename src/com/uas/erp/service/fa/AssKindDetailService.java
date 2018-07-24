package com.uas.erp.service.fa;



public interface AssKindDetailService {
	void saveAssKindDetail(String formStore, String gridStore, String caller);
	void updateAssKindDetailById(String formStore, String gridStore, String caller);
	void deleteAssKindDetail(int ak_id, String caller);
}
