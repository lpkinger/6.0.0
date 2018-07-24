package com.uas.erp.service.scm;
/**
 * 
 * @author zjh
 *
 */
public interface VendProductLossSetService {
	void saveVendProductLossSet(String formStore, String param, String caller);
	
	void updateVendProductLossSetById(String formStore, String param,  String caller);

	void deleteVendProductLossSet(int pb_id, String caller);

	String auditVendProductLossSet(int pb_id, String caller);

	void resAuditVendProductLossSet(int pb_id, String caller);

	void submitVendProductLossSet(int pb_id, String caller);

	void resSubmitVendProductLossSet(int pb_id, String caller);
}
