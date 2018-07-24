package com.uas.erp.service.pm;

public interface BOMSonService {
	void saveBOMSon(String formStore, String caller);
	void updateBOMSonById(String formStore, String caller);
	void deleteBOMSon(int bd_id, String caller);
}
