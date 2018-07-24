package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

public interface PreRecService {
	void savePreRec(String caller, String formStore, String gridStore, String param2, String param3);

	void updatePreRecById(String caller, String formStore, String gridStore, String param2, String param3);

	void deletePreRec(String caller, int pr_id);

	void printPreRec(String caller, int pr_id);

	void auditPreRec(String caller, int pr_id);

	void resAuditPreRec(String caller, int pr_id);

	void submitPreRec(String caller, int pr_id);

	void resSubmitPreRec(String caller, int pr_id);

	void postPreRec(String caller, int pr_id);

	void resPostPreRec(String caller, int pr_id);

	List<Map<String, Object>> sellerPreRec(int pr_id, String emcode, String thisamount, String caller);
}
