package com.uas.erp.service.scm;


public interface PurcVendorRateService {
	
	void savePurcVendorRate(String formStore, String gridStore, String caller);

	void updatePurcVendorRateById(String formStore, String gridStore,String  caller);

	void deletePurcVendorRate(int pvr_id, String  caller);

	void auditPurcVendorRate(int pvr_id, String  caller);

	void resAuditPurcVendorRate(int pvr_id, String  caller);

	void submitPurcVendorRate(int pvr_id, String  caller);

	void resSubmitPurcVendorRate(int pvr_id, String  caller);

}
