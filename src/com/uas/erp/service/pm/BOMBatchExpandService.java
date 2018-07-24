package com.uas.erp.service.pm;


public interface BOMBatchExpandService {
	void bomExpand(int bb_id, String gridStore,String caller);
	void updateBOMBatchExpandById(String formStore, String gridStore,String caller);
	void cleanBOMBathExpand(int bb_id,String caller);
	void bomStructAll(int emid,String caller);
	String[] printBOMSet(int id,String reportName,String condition, String caller);
}
