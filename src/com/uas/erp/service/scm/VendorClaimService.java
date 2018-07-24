package com.uas.erp.service.scm;

import java.util.Map;

public interface VendorClaimService {
	void saveVendorClaim(String caller, String formStore, String gridStore);

	void updateVendorClaim(String caller, String formStore, String gridStore);

	void deleteVendorClaim(String caller, int id);

	String auditVendorClaim(int id,String caller);

	void resAuditVendorClaim(String caller, int id);

	void submitVendorClaim(String caller, int id);

	void resSubmitVendorClaim(String caller, int id);

	String turnAPBillVendorClaim(String caller, int id);
}
