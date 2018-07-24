package com.uas.erp.service.pm;

import java.util.List;
import java.util.Map;

public interface StepTestService {

	Map<String,Object> getFormStore(String condition);

	List<Map<String,Object>> checkStep(String makecode, String stepcode, String mscode, String mccode);

	Map<String, Object> confirmQualified(String mcd_stepcode, String mc_code, String sc_code,
			String ms_code, String makecode);

	void saveBadReason(String mcd_stepcode, String mc_code, String sc_code,
			String ms_code, String bc_reason, String bc_remark);

	Map<String, Object> confirmRepairStep(String mcd_stepcode, String mc_code, String sc_code,
			String ms_code, String st_rcode);

	List<Map<String,Object>> getBadCode(String condition);

	void deleteTestBadCode(String condition);

	Map<String,Object> getSourceM(String condition);

	List<Map<String,Object>> getBadGroup();

}
