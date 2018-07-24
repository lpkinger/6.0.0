package com.uas.erp.service.pm;

import java.util.List;
import java.util.Map;

public interface SMTMoveService {

	List<Map<String,Object>> loadSMTMoveStore(String de_oldCode, String mc_code);

	void comfirmSMTMove(String de_oldCode, String mc_code, String de_newCode);

}
