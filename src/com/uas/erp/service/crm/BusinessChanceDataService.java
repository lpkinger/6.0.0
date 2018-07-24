package com.uas.erp.service.crm;

import java.util.Map;

public interface BusinessChanceDataService {

	void saveBusinessChanceData(String formStore, String caller);

	void deleteBusinessChanceData(int bcd_id, String caller);

	void updateBusinessChanceData(String formStore, String caller);

	void submitBusinessChanceData(int bcd_id, String caller);

	void resSubmitBusinessChanceData(int bcd_id, String caller);

	void auditBusinessChanceData(int bcd_id, String caller);

	void resAuditBusinessChanceData(int bcd_id, String caller);

	Map<String, Object> getAgency(String caller);
}
