package com.uas.erp.service.fa;



public interface VoucherKindService {
	void saveVoucherKind(String formStore, String caller);
	void updateVoucherKindById(String formStore, String caller);
	void deleteVoucherKind(int vk_id, String caller);
}
