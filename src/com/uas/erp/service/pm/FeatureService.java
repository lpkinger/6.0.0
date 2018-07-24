package com.uas.erp.service.pm;

public interface FeatureService {
	
	void saveFeature(String formStore, String gridStore, String caller);
	
	void updateFeatureById(String formStore, String gridStore, String caller);
	
	void deleteFeature(int vo_id, String caller);

	void resAuditFeature(int id, String caller);

	void auditFeature(int id, String caller);

	void resSubmitFeature(int id, String caller);

	void submitFeature(int id, String caller);

	int updateFeatureNameById(int id, String name, String caller);

	void addFeatureDetail(String param, String caller);

	void updateRemark(int id, String remark, String caller);

	Object checkName(String name, String caller);

	void updateDetailStatus(int id, String status, String caller);

	void updateByCondition(String tablename, String condition, String update,
			String caller);

	void addFeatureRelation(String param, String caller);
	void bannedDetails(int id, String caller);
	
}
