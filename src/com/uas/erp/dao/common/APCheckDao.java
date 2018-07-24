package com.uas.erp.dao.common;

public interface APCheckDao {
	void restoreAPBill(int id);

	void deleteAPCheck(int id);

	void restoreAPBillWithQty(int adid, double uqty);

	void updateBillStatus(Integer ac_id);
}
