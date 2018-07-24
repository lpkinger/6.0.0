package com.uas.erp.dao.common;

public interface ARCheckDao {
	void restoreARBill(int id);

	void deleteARCheck(int id);

	void restoreARBillWithQty(int andid, double uqty);

	void updateBillStatus(Integer ac_id);
}
