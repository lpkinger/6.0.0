package com.uas.erp.dao.common;

import com.uas.erp.model.DocumentPositionPower;

public interface DocumentPositionPowerDao {
	
	DocumentPositionPower getDocumentPositionPowerByJoId_DCPId(int jo_id,int dcp_id);

}
