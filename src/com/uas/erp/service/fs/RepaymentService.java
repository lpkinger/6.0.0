package com.uas.erp.service.fs;

public interface RepaymentService {

	void ConfirmRepayment(String aacode, String aakind, Double thisamount, Double backcustamount, String backdate);

}
