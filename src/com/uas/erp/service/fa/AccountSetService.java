package com.uas.erp.service.fa;

public interface AccountSetService {
	void saveAccountSet(String formStore, String caller);

	void updateAccountSetById(String formStore, String caller);

	void deleteAccountSet(int as_id, String caller);
}
