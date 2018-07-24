package com.uas.erp.service.ma;

public interface MADocumentSetupService {
	void save(String form);

	void update(String form);

	void delete(int id);

	boolean checkCaller(String caller);
}
