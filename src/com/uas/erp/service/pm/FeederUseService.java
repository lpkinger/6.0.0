package com.uas.erp.service.pm;

public interface FeederUseService {
	void getFeeder(String feedercode, String makecode, String linecode);

	void returnFeeder(String feedercode,String reason, int isuse);

	void returnAllFeeder(String makecode);

}
