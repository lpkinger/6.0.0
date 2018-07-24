package com.uas.erp.service.pm;


public interface PreProdFeatureService {
	void savePreProdFeature( String gridStore, String caller);
	void updatePreProdFeatureById(String gridStore, String caller);
	void deletePreProdFeature(int ppf_id, String caller);
}
