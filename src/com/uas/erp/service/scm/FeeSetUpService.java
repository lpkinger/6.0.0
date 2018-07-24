package com.uas.erp.service.scm;

public interface FeeSetUpService {
	void saveFeeSetUp(String formStore);
	void updateFeeSetUpById(String formStore);
	void deleteFeeSetUp(int fs_id);
}
