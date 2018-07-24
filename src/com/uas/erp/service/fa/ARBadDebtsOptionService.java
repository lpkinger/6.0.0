package com.uas.erp.service.fa;

public interface ARBadDebtsOptionService {
	void saveARBadDebtsOption(String formStore, String caller);

	void updateARBadDebtsOptionById(String formStore, String caller);

	void deleteARBadDebtsOption(int bd_id, String caller);

	void auditARBadDebtsOption(int bd_id, String caller);

	void resAuditARBadDebtsOption(int bd_id, String caller);

	void submitARBadDebtsOption(int bd_id, String caller);

	void resSubmitARBadDebtsOption(int bd_id, String caller);

}
