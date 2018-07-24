package com.uas.erp.service.hr;

import java.util.List;

import com.uas.erp.model.DocumentListPower;

public interface JobDocumentListPowerService {
	void update(String update, String caller);
	List<DocumentListPower> getDocumentListPowersByDCLID(int dcl_id);
	DocumentListPower getDLPByDclIdAndJoID(int dcl_id, int em_id);
}
