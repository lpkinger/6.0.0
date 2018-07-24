package com.uas.erp.service.fa;



public interface VoucherDescriptionService {
	void saveVoucherDescription(String formStore, String caller);
	void updateVoucherDescriptionById(String formStore, String caller);
	void deleteVoucherDescription(int vd_id, String caller);
}
