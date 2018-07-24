package com.uas.erp.dao.common;

public interface BillOutAPDao {
	void restoreAPBill(int id);
	void deleteBillOutAP(int id);
	void restoreAPBillWithQty(int andid, double uqty);
}
