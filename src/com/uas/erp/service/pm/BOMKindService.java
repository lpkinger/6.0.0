package com.uas.erp.service.pm;

public interface BOMKindService {
	void saveBOMKind(String formStore, String caller);
	void updateBOMKindById(String formStore, String caller);
	void deleteBOMKind(int bk_id, String caller);
}
