package com.uas.erp.service.pm;


public interface ProdFeatureService {
	void saveProdFeature( String gridStore, String caller);
	void updateProdFeatureById(String gridStore, String caller);
	void deleteProdFeature(int bd_id, String caller);
	void addProdFeature(String formStore, String caller);
}
