package com.uas.erp.service.pm;

public interface DevBOMDOCService {
	void saveDevBOMDOC(String formStore, String gridStore, String caller);
	void updateDevBOMDOCById(String formStore, String gridStore, String caller);
	void deleteDevBOMDOC(int bd_id, String caller);
}
