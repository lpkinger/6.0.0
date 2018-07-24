package com.uas.erp.service.pm;


public interface ECNDetailLocationService {
	void saveECNDetailLocation( String gridStore, String caller);
	void updateECNDetailLocationById(String gridStore, String caller);
	void deleteECNDetailLocation(int bd_id, String caller);
}
