package com.uas.erp.service.scm;

public interface AddressService {
	void saveAddress(String formStore, String caller);
	void updateAddressById(String formStore, String caller);
	void deleteAddress(int ad_id, String caller);
}
