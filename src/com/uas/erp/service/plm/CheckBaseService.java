package com.uas.erp.service.plm;

public interface CheckBaseService {
	void saveCheckBase(String formStore);
	void checkBaseToBug(String formStore);
    void resSubmitCheckBase(int id);
}
