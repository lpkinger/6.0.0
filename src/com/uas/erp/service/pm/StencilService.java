package com.uas.erp.service.pm;

public interface StencilService {

	void saveStencil(String formStore, String caller);

	void deleteStencil(int id, String caller);

	void updateStencilById(String formStore, String caller);

	void submitStencil(int id, String caller);

	void resSubmitStencil(int id, String caller);

	void auditStencil(int id, String caller);

	void resAuditStencil(int id, String caller);

}
