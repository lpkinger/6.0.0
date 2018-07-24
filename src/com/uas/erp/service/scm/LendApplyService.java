package com.uas.erp.service.scm;

public interface LendApplyService {
		void saveLendApply(String caller,String formStore, String param);
		void updateLendApplyById(String caller,String formStore, String param);
		void deleteLendApply(int id, String caller);
		void auditLendApply(int id, String caller);
		void submitLendApply(int id, String caller);
		void resSubmitLendApply(int id, String caller);
		void resAuditLendApply(int id, String caller);
		String addLendApply(String formdata, String data ,String caller);
}
