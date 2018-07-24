package com.uas.erp.service.pm;

public interface BOMStepChangeService {
	void saveBOMStepChange(String formStore, String gridStore, String caller); 
	void updateById(String formStore, String gridStore, String caller);
	void deleteBOMStepChange(int bo_id, String caller);
	void auditBOMStepChange(int bo_id, String caller);
	void submitBOMStepChange(int bo_id, String caller);
	void resSubmitBOMStepChange(int bo_id, String caller);
	void BOMStepChangeOpenDet(int id, String caller);
	void BOMStepChangeCloseDet(int id, String caller);
}
