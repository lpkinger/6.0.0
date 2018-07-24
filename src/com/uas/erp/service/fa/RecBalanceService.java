package com.uas.erp.service.fa;

import java.util.List;

public interface RecBalanceService {
	void catchPR(String caller, String formStore, String startdate, String enddate);

	void cleanPR(String caller, String formStore);

	void catchAB(String caller, String formStore, String startdate, String enddate, String bicode);

	void cleanAB(String caller, String formStore);

	void saveRecBalancePRDetail(String caller, String formStore, String gridStore1, String gridStore2, String assStore);

	void saveRecBalance(String caller, String formStore, String gridStore, String param2, String param3);

	void updateRecBalancePRDetailById(String caller, String formStore, String gridStore1, String gridStore2, String assStore);

	void updateRecBalanceById(String caller, String formStore, String gridStore, String param2, String param3);

	void deleteRecBalance(String caller, int rb_id);

	void printRecBalance(String caller, int rb_id);

	void auditRecBalance(String caller, int rb_id);

	void resAuditRecBalance(String caller, int rb_id);

	void submitRecBalance(String caller, int rb_id);

	void resSubmitRecBalance(String caller, int rb_id);

	void postRecBalance(String caller, int rb_id);

	void resPostRecBalance(String caller, int rb_id);

	void catchAP(String caller, String formStore, String startdate, String enddate);

	void cleanAP(String caller, String formStore);

	void catchAR(String caller, String formStore, String startdate, String enddate);

	void cleanAR(String caller, String formStore);

	void saveRecBalanceAP(String caller, String formStore, String gridStore1, String gridStore2, String gridAss1, String gridAss2);

	void updateRecBalanceAPById(String caller, String formStore, String gridStore1, String gridStore2, String gridAss1, String gridAss2);

	List<?> getPreRec(String custcode, String currency);

	List<?> getARBill(String custcode, String currency);
}
