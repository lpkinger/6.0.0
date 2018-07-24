package com.uas.erp.service.pm;

import java.util.Map;

public interface LabelPrintSettingService {

	void saveLPSetting(String formStore, String caller);

	void deleteLPSetting(int id, String caller);

	void updateLPSetting(String formStore, String caller);

	void submitLPSetting(int id, String caller);

	void resSubmitLPSetting(int id, String caller);

	void auditLPSetting(int id, String caller);

	void resAuditLPSetting(int id, String caller);

	Map<String,Object> getPrintCaller(String code, String caller);

}
