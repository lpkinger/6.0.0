package com.uas.erp.service.fa;



public interface VoucherDetailService {
	void saveVoucherDetail(String formStore, String gridStore, String caller);
	void updateVoucherDetailById(String formStore, String gridStore, String caller);
	void deleteVoucherDetail(int vo_id, String caller);
	void auditVoucherDetail(int vo_id, String caller);
	void resAuditVoucherDetail(int vo_id, String caller);
	void submitVoucherDetail(int vo_id, String caller);
	void resSubmitVoucherDetail(int vo_id, String caller);
}
