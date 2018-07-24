package com.uas.erp.service.pm;

public interface SMTFeedService {
	double getSMTFeed(String mpcode, String fecode, String mlscode, String macode, String tableAB, String barcode, String mccode, String licode, String devcode, String sccode);

	void backSMTFeed(String mlscode, String macode,String mccode, String licode, String devcode, String sccode);

	void changeSMTFeed(String mlscode, String macode, String table, String barcode, String mccode, String licode, String devcode, String sccode);
	
	void blankAll(String macode, String devcode, String mccode, String sccode);
	
	void enableDevice(String decode);
	
	void stopDevice(String decode);

	void addSMTFeed(String mlscode, String macode, String table,
			String barcode, String mccode, String licode, String devcode,
			String sccode);

	String beforeSMTFeedQuery(String caller, String condition);

	void confirmChangeMake(String mc_devcode, String mc_code,
			String mc_makecode, String mc_linecode, String mcCode, String makeCode);

	void confirmImportMPData(String mc_devcode, String mc_code,
			String mc_makecode, String mc_linecode, String mp_code,String sccode);

}
