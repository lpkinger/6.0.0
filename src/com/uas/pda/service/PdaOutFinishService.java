package com.uas.pda.service;

import java.util.List;
import java.util.Map;

public interface PdaOutFinishService {

	List<Map<String, Object>> fuzzySearch(String inoutNo);

	List<Map<String, Object>> getProdOut(String inoutNo,String pi_class);

	Map<String, Object> getNeedGetList(Long id);

	void clearGet(Long id);

	Map<String, Object> save(String barcode, int id, String kind);

	List<Map<String, Object>> getHaveSubmitList(Long id);

	void deleteDetail(Long bi_piid, String barcode, String outboxcode);

	Map<Object, Object> saveAll(String data);

}
