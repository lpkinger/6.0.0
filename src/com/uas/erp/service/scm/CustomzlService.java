package com.uas.erp.service.scm;

public interface CustomzlService {
	void saveCustomzl(String formStore, String caller);
	void updateCustomzlById(String formStore, String caller);
	void deleteCustomzl(int cz_id, String caller);
	void calculateDate(int cz_id, String caller);
}
