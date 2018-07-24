package com.uas.erp.service.pm;

import java.util.List;

public interface FeatureProductService {
	void saveFeatureProduct(String formStore, String gridStore, String caller);
	void batchSaveFeatureProduct(String bom, String detail, String caller);
	void updateFeatureProductById(String formStore, String gridStore, String caller);
	void deleteFeatureProduct(int bo_id, String caller);
	void deleteDetail(int bo_id, String caller);
	void auditFeatureProduct(int bo_id, String caller);
	void resAuditFeatureProduct(int bo_id, String caller);
	void submitFeatureProduct(int bo_id, String caller);
	void resSubmitFeatureProduct(int bo_id, String caller);
	void deleteAllDetail(int id, String caller);
	List<Object[]> getList(String tablename, String[] field, String condition,
			String caller);
}
