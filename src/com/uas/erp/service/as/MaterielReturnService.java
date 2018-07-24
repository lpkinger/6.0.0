package com.uas.erp.service.as;

public interface MaterielReturnService {
		void saveMaterielReturn(String formStore,String param, String caller);
		void updateMaterielReturnById(String formStore, String param,String caller);
		void deleteMaterielReturn(int id, String caller);
		void auditMaterielReturn(int id, String caller);
		void submitMaterielReturn(int id, String caller);
		void resSubmitMaterielReturn(int id, String caller);
		void resAuditMaterielReturn(int id, String caller);
}
