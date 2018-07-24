package com.uas.pda.service;

import java.util.List;
import java.util.Map;


public interface PdaFeederUseService {

	Map<String,Object> searchMa(String fu_makecode);

	List<Map<String,Object>> feederUsedlist(String fu_makecode, String fu_linecode);

	List<Map<String,Object>> feederMakeQuery(String fu_makecode, String fu_linecode);

	List<Map<String,Object>> feederGet(String data);

	void feederBack(String data);

	void feederBackAll(String fu_makecode);

}
