package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

public interface AccountCheckService {
	void saveAccountCheck(String caller, String formStore, String param);

	void updateAccountCheck(String caller, String formStore, String param);

	void deleteAccountCheck(int acc_id, String caller);

	void submitAccountCheck(int acc_id, String caller);

	void resSubmitAccountCheck(int acc_id, String caller);

	void auditAccountCheck(int acc_id, String caller);

	void resAuditAccountCheck(int acc_id, String caller);

	List<Map<String, Object>> getAccountCheck(String caller, int yearmonth, String status, String catecode);

	List<Map<String, Object>> getAccountRegister(String caller, int yearmonth, String status, String catecode);

	void autoCheck(int yearmonth, String caller);

	void confirmCheck(String data1, String data2);

	void cancelCheck(String data1, String data2);
}
