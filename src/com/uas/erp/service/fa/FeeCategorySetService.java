package com.uas.erp.service.fa;

public interface FeeCategorySetService {
	void saveFeeCategorySet(String formStore, String caller);
	void updateFeeCategorySetById(String formStore, String caller);
	void deleteFeeCategorySet(int fcs_id, String caller);
}
