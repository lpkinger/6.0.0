package com.uas.erp.service.oa;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;

public interface FeePleaseService {
	Object saveFeePlease(String formStore, String gridStore, String gridStore2, String caller);

	Object updateFeePlease(String formStore, String gridStore, String gridStore2, String caller);

	void deleteFeePlease(int fp_id, String caller);

	void auditFeePlease(int fp_id, String caller);

	void resAuditFeePlease(int fp_id, String caller);

	void submitFeePlease(int fp_id, String caller);

	void resSubmitFeePlease(int fp_id, String caller);

	void endFeePlease(int fp_id, String caller);

	void resEndFeePlease(int fp_id, String caller);

	Map<String,Object> turnCLFBX(int fp_id, String caller);

	int turnFYBX(int fp_id, String caller);

	int turnFYBX2(int fp_id, String caller);

	int turnYHFKSQ(int fp_id, String caller);

	int turnYWZDBX(int fp_id, String caller);

	int jksqturnFYBX(int fp_id, String caller);

	String sealTurnFYBX(int fp_id, String caller, double thispayamount);

	String[] printFeePlease(int fp_id, String reportName, String condition, String caller);

	void confirmFeePlease(int id, String caller);

	String turnBankRegister(int id, String paymentcode, String payment, double thispayamount, String caller);

	String turnBillAP(int id, String paymentcode, String payment, double thispayamount, String caller);

	String turnBillARChange(int id, String paymentcode, String payment, double thispayamount, String caller);

	void updateFactdays(String data);

	List<JSONTree> getJsonTrees(int parentid);

	Object getContractTypeNum(int id, String table);

	Object getContractTypeNumByKind(String k1, String k2, String k3, String k4);

	String vastTurnFYBX(String caller, String data);

	JSONObject getFeeAccount(String emname);

	List<JSONTree> getJSONTreeBySearch(String search, Employee employee);

	void saveOutAddress(String formStore);

	void checkTime(Map<Object, Object> formStore);

	String getFromSob(String condition);
}
