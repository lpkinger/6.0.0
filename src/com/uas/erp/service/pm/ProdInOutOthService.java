package com.uas.erp.service.pm;

import java.util.Map;

public interface ProdInOutOthService {

	void updateProdInOutById(String formStore, String gridStore, String caller);

	void deleteProdInOut(int pd_id, String caller);

	void printProdInOut(int pd_id, String caller);

	void auditProdInOut(int pd_id, String caller);

	void resAuditProdInOut(int pd_id, String caller);

	void submitProdInOut(int pd_id, String caller);

	void resSubmitProdInOut(int pd_id, String caller);

	void postProdInOut(int pd_id, String caller);

	void resPostProdInOut(int pd_id, String caller);

	void saveProdIOClash(String caller, String data, int id, double clashqty);
	
	void setProdIOClash (int id, String  caller);

	Map<String, Object> getClashInfo(String caller, String con);
}
