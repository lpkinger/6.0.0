package com.uas.erp.service.as;

public interface MaterielApplyService {
		void saveMaterielApply(String formStore, String param,String caller);
		void updateMaterielApplyById(String formStore,String param, String caller);
		void deleteMaterielApply(int id, String caller);
		void auditMaterielApply(int id, String caller);
		void submitMaterielApply(int id, String caller);
		void resSubmitMaterielApply(int id, String caller);
		void resAuditMaterielApply(int id, String caller);
}
