package com.uas.erp.service.plm;

public interface MileStoneService {
	void saveMileStone(String formStore, String gridStore);

	void deleteMileStone(int ca_id);

	void updateMileStone(String formStore, String gridStore);

	void submitMileStone(int ca_id);

	void resSubmitMileStone(int ca_id);

	void auditMileStone(int ca_id);

	void resAuditMileStone(int ca_id);
}
