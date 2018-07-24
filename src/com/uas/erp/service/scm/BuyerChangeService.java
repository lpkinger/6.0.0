package com.uas.erp.service.scm;

public interface BuyerChangeService {

	void saveBuyerChange(String caller, String formStore, String param);

	void submitBuyerChange(String caller, int id);

	void updateBuyerChangeById(String formStore, String param, String caller);

	void resSubmitBuyerChange(String caller, int id);

	void auditBuyerChange(int id, String caller);

	void deleteBuyerChange(String caller, int id);

}
