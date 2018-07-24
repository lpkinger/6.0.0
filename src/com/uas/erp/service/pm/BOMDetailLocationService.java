package com.uas.erp.service.pm;

public interface BOMDetailLocationService {
	void saveBOMDetailLocation( String gridStore, String caller,String formStore);
	void updateBOMDetailLocationById(String gridStore, String caller,String formStore);
	void deleteBOMDetailLocation(int bd_id, String caller);
}
