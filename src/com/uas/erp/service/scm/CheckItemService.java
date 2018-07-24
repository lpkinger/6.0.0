package com.uas.erp.service.scm;

public interface CheckItemService {
	void saveCheckItem(String formStore, String caller);
	void updateCheckItemById(String formStore, String caller);
	void deleteCheckItem(int ci_id, String caller);
	void printCheckItem(int ci_id, String caller);
}
