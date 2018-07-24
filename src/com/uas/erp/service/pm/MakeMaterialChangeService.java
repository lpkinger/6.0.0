package com.uas.erp.service.pm;


public interface MakeMaterialChangeService {
	void saveMakeMaterialChange(String formStore, String gridStore, String caller);
	void updateMakeMaterialChangeById(String formStore, String gridStore, String caller);
	void deleteMakeMaterialChange(int mc_id, String caller);
	void auditMakeMaterialChange(int mc_id, String caller);
	void resAuditMakeMaterialChange(int mc_id, String caller);
	void submitMakeMaterialChange(int mc_id, String caller);
	void resSubmitMakeMaterialChange(int mc_id, String caller);
	void updateMakeMaterialChangeInProcss(String formStore, String param,
			String caller);
	void MakeMaterialChangeCloseDet(int id, String caller);
	void MakeMaterialChangeOpenDet(int id, String caller);
	void MakeMaterialChangeCloseAll(int id, String caller);
	String makeMaterialChangeTurnProdIOReturn(int id, String caller);
}
