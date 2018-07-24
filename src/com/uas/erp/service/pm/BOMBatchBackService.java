package com.uas.erp.service.pm;


public interface BOMBatchBackService {
	void bomBack(String gridStore,String caller);
	void updateBOMBatchBackById(String formStore, String gridStore,String caller);
	void cleanBOMBatchBack(int em_id,String caller);
}
