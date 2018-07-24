package com.uas.erp.service.pm;

public interface StencilUseService {

	void saveStencilUse(String formStore, String caller);

	void deleteStencilUse(int id, String caller);

	void updateStencilUse(String formStore, String caller);

	void submitStencilUse(int id, String caller);

	void resSubmitStencilUse(int id, String caller);

	void auditStencilUse(int id, String caller);

	void resAuditStencilUse(int id, String caller);

	void backStencil(int id, String caller, String record, String location,
			String date);

}
