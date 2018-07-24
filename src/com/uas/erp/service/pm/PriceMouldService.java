package com.uas.erp.service.pm;

import java.util.List;
import java.util.Map;

public interface PriceMouldService {
	void savePriceMould(String caller, String formStore, String param, String param2);

	void updatePriceMouldById(String caller, String formStore, String param, String param2);

	void deletePriceMould(int pd_id, String caller);

	void printPriceMould(int pd_id, String caller);

	void auditPriceMould(int pd_id, String caller);

	void resAuditPriceMould(int pd_id, String caller);

	void submitPriceMould(int pd_id, String caller);

	void resSubmitPriceMould(int pd_id, String caller);

	List<Map<String, Object>> turnInquiry(int pd_id, String caller);

	int turnPurMould(int pd_id, String caller);
}
