package com.uas.erp.service.pm;

public interface BOMSetChangeService {
	void saveBOMSetChange(String formStore, String gridStore,String caller);
	void updateById(String formStore,String gridStore, String caller);
	void deleteBOMSetChange(int bs_id, String caller);
	void auditBOMSetChange(int bs_id, String caller);
	void submitBOMSetChange(int bs_id, String caller);
	void resSubmitBOMSetChange(int bs_id, String caller);
}
