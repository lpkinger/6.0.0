package com.uas.erp.service.pm;

public interface MakeStepChangeService {
	void saveMakeStepChange(String formStore, String gridStore, String caller); 
	void updateById(String formStore, String gridStore, String caller);
	void deleteMakeStepChange(int bo_id, String caller);
	void auditMakeStepChange(int bo_id, String caller);
	void submitMakeStepChange(int bo_id, String caller);
	void resSubmitMakeStepChange(int bo_id, String caller);
	void MakeStepChangeOpenDet(int id, String caller);
	void MakeStepChangeCloseDet(int id, String caller);
}
