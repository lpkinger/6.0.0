package com.uas.erp.service.pm;


public interface BOMDetailService {
	void saveBOMDetail(String formStore, String caller);
	void updateBOMDetailById(String formStore, String caller);
	void deleteBOMDetail(int bd_id, String caller);
}
