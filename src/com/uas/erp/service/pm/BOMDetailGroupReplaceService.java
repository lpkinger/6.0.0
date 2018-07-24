package com.uas.erp.service.pm;

public interface BOMDetailGroupReplaceService {
	void saveBOMDetailGroupReplace(String formStore, String gridStore, String caller);
	void updateBOMDetailGroupReplaceById(String formStore, String gridStore, String caller);
	void deleteBOMDetailGroupReplace(int bdg_id, String caller);
}
