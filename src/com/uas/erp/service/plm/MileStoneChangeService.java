package com.uas.erp.service.plm;

public interface MileStoneChangeService {
		void saveMileStoneChange(String formStore, String caller);
		void updateMileStoneChangeById(String formStore, String caller);
		void deleteMileStoneChange(int id, String caller);
		void auditMileStoneChange(int id, String caller);
		void submitMileStoneChange(int id, String caller);
		void resSubmitMileStoneChange(int id, String caller);
		void resAuditMileStoneChange(int id, String caller);
}
