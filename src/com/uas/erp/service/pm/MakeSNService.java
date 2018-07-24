package com.uas.erp.service.pm;

public interface MakeSNService {

	void deleteMakeSN(int ma_id, String caller);
	
	void occurCode(int id, String prefixcode, String suffixcode, String startno, int number);

	String checkOrNewBarcode(boolean newSerial, String serialCode, int ma_id);

}
