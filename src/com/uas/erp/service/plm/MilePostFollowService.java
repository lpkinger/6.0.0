package com.uas.erp.service.plm;

public interface MilePostFollowService {
		void saveMilePostFollow(String formStore, String caller);
		void updateMilePostFollowById(String formStore, String caller);
		void deleteMilePostFollow(int id, String caller);
		void auditMilePostFollow(int id, String caller);
		void submitMilePostFollow(int id, String caller);
		void resSubmitMilePostFollow(int id, String caller);
		void resAuditMilePostFollow(int id, String caller);
}
