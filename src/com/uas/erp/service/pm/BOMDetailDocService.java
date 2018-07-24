package com.uas.erp.service.pm;


public interface BOMDetailDocService {
	void saveBOMDetailDoc(String gridStore, String caller);
	void updateBOMDetailDocById(String gridStore, String caller);
	void deleteBOMDetailDoc(int bd_id, String caller);
}
