package com.uas.erp.service.hr;


public interface HrHeadShipService {
	void saveHrHeadShip(String formStore, String  caller);
	void updateHrHeadShipById(String formStore, String  caller);
	void deleteHrHeadShip(int hs_id, String  caller);
}
