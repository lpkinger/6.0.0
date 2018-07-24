package com.uas.erp.service.pm;

public interface MakeSerialService {

	void updateMakeSerialById(String formStore, String gridStore, String caller);

	void deleteMakeSerial(int ma_id, String caller);
	
	void occurCode(int id, String prefixcode, String suffixcode, String startno, int number, int combineqty);

	String checkOrNewBarcode(boolean newSerial, String serialCode, int mc_id);

}
