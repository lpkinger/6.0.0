package com.uas.erp.service.pm;

import java.util.Map;

public interface MakePrepareService {
	void saveMakePrepare(String formStore, String caller);

	void updateMakePrepareById(String formStore, String caller);

	void auditMakePrepare(int mp_id, String caller);

	void resAuditMakePrepare(int mp_id, String caller);
    
	Map<String, Object> returnBar(String barcode, int mpid);

	void deleteMakePrepare(int id, String caller);

	void submitMakePrepare(int id, String caller);

	void resSubmitMakePrepare(int id, String caller);

	void toProdIOGet(int id,String caller);
	
	Map<String,Object> getBar(String barcode, String whcode, int maid, int mpid);
}
