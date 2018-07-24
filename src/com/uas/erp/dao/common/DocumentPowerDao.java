package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.DocumentPositionPower;
import com.uas.erp.model.DocumentPower;

public interface DocumentPowerDao {
	
	DocumentPower queryDocumentPowerById(int id);
	List<DocumentPositionPower> getDocumentPositionPowersByDCPID(int dcp_id);
	DocumentPositionPower getDPPByDcpIdAndJoId(int dcp_id, int jo_id);

}
