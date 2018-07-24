package com.uas.erp.service.pm;


public interface ECRDetailLocationService {
	void saveECRDetailLocation( String gridStore, String caller);
	void updateECRDetailLocationById(String gridStore, String caller);
	void deleteECRDetailLocation(int bd_id, String caller);
}
