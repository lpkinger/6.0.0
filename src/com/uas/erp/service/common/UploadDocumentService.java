package com.uas.erp.service.common;

public interface UploadDocumentService {

	public abstract void saveUploadDocument(String formStore, String gridStore,
			String caller);

	public abstract void deleteUploadDocument(int ud_id, String caller);

	public abstract void updateUploadDocument(String formStore,
			String gridStore, String caller);

	public abstract void submitUploadDocumentById(int udId, String caller);

	public abstract void resSubmitUploadDocument(int udId, String caller);

	public abstract void auditUploadDocument(int udId, String caller);

	public abstract void resAuditUploadDocument(int udId, String caller);

}