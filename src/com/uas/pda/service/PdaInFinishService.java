package com.uas.pda.service;

import java.util.List;
import java.util.Map;

public interface PdaInFinishService {

	List<Map<String, Object>> fuzzySearch(String inoutNo, String whcode);

	List<Map<String, Object>> getProdIn(String inoutNo, String whcode);

	Map<String, Object> getNeedGetList(Long pi_id, String whcode);

	Map<String, Object> save(Long pd_piid, String whcode, String barcode, String kind);

	void deleteDetail(Long bi_piid, String barcode, String outboxcode, String whcode);

	Map<Object, Object> saveAll(String data);

}
