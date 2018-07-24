package com.uas.erp.dao.common;

public interface BillOutDao {
	void restoreARBill(int id);
	void deleteBillOut(int id);
	void restoreARBillWithQty(int andid, double uqty);
}
