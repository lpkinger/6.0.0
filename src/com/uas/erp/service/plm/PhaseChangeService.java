package com.uas.erp.service.plm;

import java.util.List;
import java.util.Map;


public interface PhaseChangeService {
	void savePhaseChange(String caller,String formStore,String param);
	void updatePhaseChange(String caller,String formStore,String param);
	void deletePhaseChange(String caller,int id);
	void submitPhaseChange(String caller,int id);
	void resSubmitPhaseChange(String caller,int id);
	void auditPhaseChange(int id,String caller);
	List<Map<String,Object>> loadPhase(String prj_code);
}
