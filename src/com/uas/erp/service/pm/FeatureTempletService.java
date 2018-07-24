package com.uas.erp.service.pm;

public interface FeatureTempletService {
	void saveFeatureTemplet(String formStore, String gridStore, String caller);
	void updateFeatureTempletById(String formStore, String gridStore, String caller);
	void deleteFeatureTemplet(int ft_id, String caller);
	void auditFeatureTemplet(int ft_id, String caller);
	void resAuditFeatureTemplet(int ft_id, String caller);
	void submitFeatureTemplet(int ft_id, String caller);
	void resSubmitFeatureTemplet(int ft_id, String caller);
}
