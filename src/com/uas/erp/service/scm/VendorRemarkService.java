package com.uas.erp.service.scm;

public interface VendorRemarkService {
	void saveVendorRemark(String formStore, String gridStore, String caller);
	void updateVendorRemarkById(String formStore, String gridStore, String caller);
	void deleteVendorRemark(int vr_id, String caller);
	void bannedVendorRemark(int vr_id, String caller);
	void resBannedVendorRemark(int vr_id, String caller);
}
