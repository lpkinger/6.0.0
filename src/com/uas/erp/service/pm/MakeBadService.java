package com.uas.erp.service.pm;

import java.util.Map;

public interface MakeBadService {

	Map<String,Object> checkSNcode(String ms_sncode, String st_code);

	void deleteMakeBad(int mb_id);

	String addOrUpdateMakeBad(String data);

	void finishFix(String data);

	void makeBadScrap(String data);

}
