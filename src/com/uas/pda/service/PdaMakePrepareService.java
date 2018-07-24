package com.uas.pda.service;

import java.util.List;
import java.util.Map;

public interface PdaMakePrepareService {

	Map<String,Object> searchMp(String mp_code, String type);

	List<Map<String,Object>> barcodeList(int mp_id);

	List<Map<String,Object>> needPreparedList(int mp_id);

	Map<String,Object> barGet(String barcode);

	Map<String, Object> barBack(String barcode, int mpid);

	List<Map<String,Object>> getMpcodeList(String type);

	String makePrepareFeederGet(String data);

	List<Map<String,Object>> preparedFeederList(int mp_id);

	void makePrepareFeederBack(String bar_code, int mp_id);

	void updateChecked(String code);

}
