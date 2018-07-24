package com.uas.erp.service.as;

public interface MaterielOutService {
		void saveMaterielOut(String formStore, String param,String caller);
		void updateMaterielOutById(String formStore, String param,String caller);
		void deleteMaterielOut(int id, String caller);
		void auditMaterielOut(int id, String caller);
		void submitMaterielOut(int id, String caller);
		void resSubmitMaterielOut(int id, String caller);
		void resAuditMaterielOut(int id, String caller);
		void updateMaterialQtyChangeInProcss(String caller,String formStore, String param);
}
