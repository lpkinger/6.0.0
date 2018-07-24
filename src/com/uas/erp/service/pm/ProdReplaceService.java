package com.uas.erp.service.pm;


public interface ProdReplaceService {
	void saveProdReplace(String gridStore, String caller);
	void updateProdReplaceById(String gridStore, String caller);
	void deleteProdReplace(int bd_id, String caller);
	void setMain(int pre_id, String caller);
}
