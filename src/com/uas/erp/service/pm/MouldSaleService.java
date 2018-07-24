package com.uas.erp.service.pm;

public interface MouldSaleService {
	void saveMouldSale(String formStore, String gridStore, String caller);
	void updateMouldSaleById(String formStore, String gridStore, String caller);
	void deleteMouldSale(int msa_id, String caller);
	void printMouldSale(int msa_id, String caller);
	void auditMouldSale(int msa_id, String caller);
	void resAuditMouldSale(int msa_id, String caller);
	void submitMouldSale(int msa_id, String caller);
	void resSubmitMouldSale(int msa_id, String caller);
	String turnDeliveryOrder(int msa_id, String caller);
	void updateChargeStatus(int msa_id, String status, String remark, String caller);
}
