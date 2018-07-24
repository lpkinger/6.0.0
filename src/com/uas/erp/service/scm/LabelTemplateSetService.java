package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

public interface LabelTemplateSetService {

	void saveLabelT(String formStore, String param, String caller);

	void deleteLabelT(String caller, int id);

	void updateLabelT(String formStore, String param, String caller);

	void auditLabelT(String caller, int id);

	void resAuditLabelT(String caller, int id);

	void bannedSerial(String caller, int id);

	void resBannedLabelT(String caller, int id);

	void submitLabelT(String caller, int id);

	void resSubmitLabelT(String caller, int id);

	void saveLabelP(String formStore, String caller);

	void updateLabelP(String caller, String formStore);

	void deleteLabelP(String caller, int id);

	List<Map<String, Object>> getdetail(String caller, String condition);

	void deleteLabelPrintSetting(String caller, String lps_caller);

	void saveLPrintSetting(String caller, String param);

}
