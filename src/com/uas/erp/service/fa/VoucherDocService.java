package com.uas.erp.service.fa;



public interface VoucherDocService {
	void saveVoucherDoc(String formStore, String gridStore, String caller);
	void updateVoucherDocById(String formStore, String gridStore, String caller);
	void deleteVoucherDoc(int vo_id, String caller);
	void auditVoucherDoc(int vo_id, String caller);
	void resAuditVoucherDoc(int vo_id, String caller);
	void submitVoucherDoc(int vo_id, String caller);
	void resSubmitVoucherDoc(int vo_id, String caller);
}
