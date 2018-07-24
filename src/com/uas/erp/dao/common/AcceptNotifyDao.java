package com.uas.erp.dao.common;

public interface AcceptNotifyDao {
	void restorePurc(int id);
	void deleteAcceptNotify(int id);
	void checkQty(int id);
	void restorePurcWithQty(int andid, double uqty);
	int turnVerifyApply(int id);
	int turnProdio(int id);
}
