package com.uas.erp.service.pm;

public interface BOMDetailGroupService {
	void saveBOMDetailGroup(String formStore, String caller);
	void updateBOMDetailGroupById(String formStore, String caller);
	void deleteBOMDetailGroup(int bdg_id, String caller);
}
