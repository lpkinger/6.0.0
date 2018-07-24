package com.uas.erp.service.pm;

public interface CommonReplaceService {
	void saveCommonReplaceService(String gridStore, String caller);
	void updateCommonReplaceServiceById(String formStore,String gridStore, String caller);
	void deleteCommonReplaceService(int pr_id, String caller);
}
