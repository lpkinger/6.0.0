package com.uas.erp.service.fa;

public interface AccountRegisterImportService {
	void saveAccountRegisterImportById(String formStore, String gridStore, String caller);

	void updateAccountRegisterImportById(String formStore, String gridStore, String caller);

	void delete(int emid, String caller);

	void cleanAccountRegisterImport(int emid, String caller);

	void cleanFailed(int emid, String caller);

	void accountRegisterImport(int emid);
}
