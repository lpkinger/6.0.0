package com.uas.erp.service.scm;

public interface VendorContactService {
	void updateVendor(String formStore);

	void saveVendContact(String formStore, String caller);

	void updateVendContact(String formStore, String caller);
}
