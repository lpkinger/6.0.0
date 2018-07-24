package com.uas.erp.service.fa;

import java.util.List;

public interface PayBalanceService {

	void cleanAB(String caller, String formStore);

	void savePayBalance(String caller, String formStore, String gridStore, String param2, String param3);

	void updatePayBalanceById(String caller, String formStore, String gridStore, String param2, String param3);

	void deletePayBalance(String caller, int pb_id);

	String[] printPayBalance(int pu_id, String reportName, String condition, String caller);

	void auditPayBalance(String caller, int pb_id);

	void resAuditPayBalance(String caller, int pb_id);

	void submitPayBalance(String caller, int pb_id);

	void resSubmitPayBalance(String caller, int pb_id);

	void postPayBalance(String caller, int pb_id);

	void resPostPayBalance(String caller, int pb_id);

	void savePayBalancePRDetail(String caller, String formStore, String gridStore1, String gridStore2, String assStore);

	void updatePayBalancePRDetailById(String caller, String formStore, String gridStore1, String gridStore2, String assStore);

	void catchPP(String caller, String formStore, String startdate, String enddate);

	void cleanPP(String caller, String formStore);

	void catchAP(String caller, String formStore);

	void cleanAP(String caller, String formStore);

	void catchAR(String caller, String formStore);

	void cleanAR(String caller, String formStore);

	void savePayBalanceAR(String caller, String formStore, String gridStore1, String gridStore2);

	void updatePayBalanceARById(String caller, String formStore, String gridStore1, String gridStore2);

	List<?> getPrePay(String vendcode, String currency);

	List<?> getAPBill(String vendcode, String currency);

	void catchAB(String caller, String formStore, String startdate,
			String enddate, String bicode);

}
