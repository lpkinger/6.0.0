package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.DocumentListPower;

public interface DocumentListPowerDao {
	
	DocumentListPower getDLPByJoId_DclId(int jo_id,int dcl_id);
	List<DocumentListPower> getDocumentListPowersByDCLID(int dcl_id);

}
