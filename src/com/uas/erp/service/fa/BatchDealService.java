package com.uas.erp.service.fa;

public interface BatchDealService {
	String vastTurnARBill(String caller, String data);

	String vastTurnAPBill(String caller, String data);

	String vastARBillPost(String caller, String data);

	String vastTurnBillAP(String caller, String data);

	String vastTurnRecBalance(String caller, String data);

	String vastTurnPayBalance(String caller, String data);

	String vastALMonthUpdate(String caller, String data);

	String vastToPBorPP(String caller, String data);

	String vastTurnBillOut(String caller, String data);

	String vastTurnBillOutAP(String caller, String data);

	String vastSubmitAccountRegister(String caller, String data);

	String vastConfirmCheckRegister(String caller, String data);

	String vastCancelCheckRegister(String caller, String data);

	void faPost(String caller, String from, String to, String pclass);

	String vastTurnARCheck(String caller, String data, String fromDate, String toDate);

	String vastTurnAPCheck(String caller, String data, String fromDate, String toDate);

	void confirmPrePayAPBill(int vmid, double thisamount, String data1, String data2);

	void confirmPreRecARBill(int cmid, double thisamount, String data1, String data2);

	String vastARCheckConfirm(String caller, String data);

	void anticipateCollection(String caller, String data);
}
