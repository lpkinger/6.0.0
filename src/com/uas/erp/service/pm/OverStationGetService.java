package com.uas.erp.service.pm;

import java.util.Map;

public interface OverStationGetService {

	Map<String,Object> getOverStationStore(String scCode, String mcCode);

	Map<String,Object> confirmSnCodeGet(String sc_code, String mc_code, String sn_code,
			String st_code, boolean combineChecked);

}
