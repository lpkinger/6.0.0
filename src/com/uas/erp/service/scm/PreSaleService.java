package com.uas.erp.service.scm;

import java.util.Map;

public interface PreSaleService {
	void savePreSale(String formStore, String caller);
	void updatePreSaleById(String formStore, String caller);
	void deletePreSale(int ps_id, String caller);
	void auditPreSale(int ps_id, String caller);
	void resAuditPreSale(int ps_id, String caller);
	void submitPreSale(int ps_id, String caller);
	void resSubmitPreSale(int ps_id, String caller);
	int turnSale(int ps_id, String caller);
	Map<String , Object > getOtherPreSaleValues(int ps_id);
	String turnPreSaleToSale(int ps_id, String type);
}
