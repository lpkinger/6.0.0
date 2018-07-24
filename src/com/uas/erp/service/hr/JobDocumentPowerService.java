package com.uas.erp.service.hr;

import java.util.List;

import com.uas.erp.model.DocumentPositionPower;

public interface JobDocumentPowerService {
	void update(String update, String caller);
	List<DocumentPositionPower> getDocumentPositionPowersByDCPID(int dcp_id);
	DocumentPositionPower getDPPByDcpIdAndJoID(int dcp_id, int jo_id);
}
